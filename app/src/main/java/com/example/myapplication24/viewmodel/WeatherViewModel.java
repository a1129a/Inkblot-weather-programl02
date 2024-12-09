package com.example.myapplication24.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication24.api.RetrofitClient;
import com.example.myapplication24.cache.WeatherCache;
import com.example.myapplication24.model.AirQuality;
import com.example.myapplication24.model.WeatherDaily;
import com.example.myapplication24.model.WeatherHourly;
import com.example.myapplication24.model.WeatherNow;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherViewModel extends AndroidViewModel {
    private static final String TAG = "WeatherViewModel";
    private final MutableLiveData<WeatherNow> currentWeather = new MutableLiveData<>();
    private final MutableLiveData<List<WeatherDaily>> dailyForecast = new MutableLiveData<>();
    private final MutableLiveData<List<WeatherHourly>> hourlyForecast = new MutableLiveData<>();
    private final MutableLiveData<AirQuality> airQuality = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final AtomicInteger activeRequests = new AtomicInteger(0);
    private final Gson gson = new Gson();
    private final WeatherCache weatherCache;
    private String currentLocation;
    private boolean isRequestInProgress = false;
    private static final long REQUEST_COOLDOWN = 2000; // 2秒冷却时间
    private long lastRequestTime = 0;

    public WeatherViewModel(Application application) {
        super(application);
        weatherCache = new WeatherCache(application);
    }

    public void fetchWeatherData(String location) {
        // 检查是否正在请求中
        if (isRequestInProgress) {
            Log.d(TAG, "Weather data request already in progress");
            return;
        }

        // 检查请求冷却时间
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRequestTime < REQUEST_COOLDOWN) {
            Log.d(TAG, "Weather data request is in cooldown period");
            return;
        }

        // 检查缓存
        if (location.equals(currentLocation) && weatherCache.isCacheValid()) {
            Log.d(TAG, "Loading weather data from cache");
            loadFromCache();
            return;
        }

        isRequestInProgress = true;
        lastRequestTime = currentTime;
        currentLocation = location;
        activeRequests.set(4); // 重置为4个请求
        isLoading.postValue(true);

        try {
            Log.d(TAG, "Starting weather data requests for location: " + location);
            fetchCurrentWeather(location);
            fetchDailyForecast(location);
            fetchHourlyForecast(location);
            fetchAirQuality(location);
        } catch (Exception e) {
            Log.e(TAG, "Error initiating weather data requests", e);
            errorMessage.postValue("请求出错: " + e.getMessage());
            resetRequestState();
        }
    }

    private void resetRequestState() {
        isRequestInProgress = false;
        activeRequests.set(0);
        isLoading.postValue(false);
    }

    private void loadFromCache() {
        WeatherNow cachedNow = weatherCache.getNowWeather();
        List<WeatherDaily> cachedDaily = weatherCache.getDailyWeather();
        List<WeatherHourly> cachedHourly = weatherCache.getHourlyWeather();
        AirQuality cachedAir = weatherCache.getAirQuality();

        if (cachedNow != null)
            currentWeather.setValue(cachedNow);
        if (cachedDaily != null)
            dailyForecast.setValue(cachedDaily);
        if (cachedHourly != null)
            hourlyForecast.setValue(cachedHourly);
        if (cachedAir != null)
            airQuality.setValue(cachedAir);
    }

    public void fetchCurrentWeather(String location) {
        try {
            Log.d(TAG, "Fetching current weather for location: " + location);
            RetrofitClient.getInstance().getWeatherApi().getNowWeather(location)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                try {
                                    String responseBody = response.body();
                                    JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

                                    if (jsonObject.has("code") && "200".equals(jsonObject.get("code").getAsString())) {
                                        JsonObject nowObject = jsonObject.getAsJsonObject("now");
                                        WeatherNow weather = gson.fromJson(nowObject, WeatherNow.class);
                                        currentWeather.postValue(weather);
                                        weatherCache.saveNowWeather(weather);
                                    } else {
                                        String code = jsonObject.has("code") ? jsonObject.get("code").getAsString()
                                                : "unknown";
                                        String message = jsonObject.has("message")
                                                ? jsonObject.get("message").getAsString()
                                                : "";
                                        errorMessage.postValue("API错误: " + code + " - " + message);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing current weather data", e);
                                    errorMessage.postValue("数据解析错误: " + e.getMessage());
                                }
                            } else {
                                Log.e(TAG, "Error response: " + response.code());
                                errorMessage.postValue("获取天气数据失败: " + response.code());
                            }
                            checkAndUpdateLoadingState();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e(TAG, "Network request failed", t);
                            errorMessage.postValue("网络请求失败: " + t.getMessage());
                            // 如果网络请求失败，尝试使用缓存数据
                            WeatherNow cached = weatherCache.getNowWeather();
                            if (cached != null) {
                                currentWeather.postValue(cached);
                            }
                            checkAndUpdateLoadingState();
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in fetchCurrentWeather", e);
            errorMessage.postValue("请求出错: " + e.getMessage());
            checkAndUpdateLoadingState();
        }
    }

    public void fetchDailyForecast(String location) {
        try {
            Log.d(TAG, "Fetching daily forecast for location: " + location);
            RetrofitClient.getInstance().getWeatherApi().getDailyWeather(location)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                try {
                                    String responseBody = response.body();
                                    JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

                                    if (jsonObject.has("code") && "200".equals(jsonObject.get("code").getAsString())) {
                                        JsonArray dailyArray = jsonObject.getAsJsonArray("daily");
                                        List<WeatherDaily> forecasts = new ArrayList<>();
                                        for (JsonElement element : dailyArray) {
                                            WeatherDaily daily = gson.fromJson(element, WeatherDaily.class);
                                            forecasts.add(daily);
                                        }
                                        dailyForecast.postValue(forecasts);
                                        weatherCache.saveDailyWeather(forecasts);
                                    } else {
                                        String code = jsonObject.has("code") ? jsonObject.get("code").getAsString()
                                                : "unknown";
                                        String message = jsonObject.has("message")
                                                ? jsonObject.get("message").getAsString()
                                                : "";
                                        errorMessage.postValue("API错误: " + code + " - " + message);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing daily forecast data", e);
                                    errorMessage.postValue("数据解析错误: " + e.getMessage());
                                }
                            } else {
                                Log.e(TAG, "Error response: " + response.code());
                                errorMessage.postValue("获取天气预报失败: " + response.code());
                            }
                            checkAndUpdateLoadingState();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e(TAG, "Network request failed", t);
                            errorMessage.postValue("网络请求失败: " + t.getMessage());
                            // 如果网络请求失败，尝试使用缓存数据
                            List<WeatherDaily> cached = weatherCache.getDailyWeather();
                            if (cached != null) {
                                dailyForecast.postValue(cached);
                            }
                            checkAndUpdateLoadingState();
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in fetchDailyForecast", e);
            errorMessage.postValue("请求出错: " + e.getMessage());
            checkAndUpdateLoadingState();
        }
    }

    public void fetchHourlyForecast(String location) {
        try {
            Log.d(TAG, "Fetching hourly forecast for location: " + location);
            RetrofitClient.getInstance().getWeatherApi().getHourlyWeather(location)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                try {
                                    String responseBody = response.body();
                                    JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

                                    if (jsonObject.has("code") && "200".equals(jsonObject.get("code").getAsString())) {
                                        JsonArray hourlyArray = jsonObject.getAsJsonArray("hourly");
                                        List<WeatherHourly> forecasts = new ArrayList<>();
                                        for (JsonElement element : hourlyArray) {
                                            WeatherHourly hourly = gson.fromJson(element, WeatherHourly.class);
                                            forecasts.add(hourly);
                                        }
                                        hourlyForecast.postValue(forecasts);
                                        weatherCache.saveHourlyWeather(forecasts);
                                    } else {
                                        String code = jsonObject.has("code") ? jsonObject.get("code").getAsString()
                                                : "unknown";
                                        String message = jsonObject.has("message")
                                                ? jsonObject.get("message").getAsString()
                                                : "";
                                        errorMessage.postValue("API错误: " + code + " - " + message);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing hourly forecast data", e);
                                    errorMessage.postValue("数据解析错误: " + e.getMessage());
                                }
                            } else {
                                Log.e(TAG, "Error response: " + response.code());
                                errorMessage.postValue("获取小时预报失败: " + response.code());
                            }
                            checkAndUpdateLoadingState();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e(TAG, "Network request failed", t);
                            errorMessage.postValue("网络请求失败: " + t.getMessage());
                            // 如果网络请求失败，尝试使用缓存数据
                            List<WeatherHourly> cached = weatherCache.getHourlyWeather();
                            if (cached != null) {
                                hourlyForecast.postValue(cached);
                            }
                            checkAndUpdateLoadingState();
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in fetchHourlyForecast", e);
            errorMessage.postValue("请求出错: " + e.getMessage());
            checkAndUpdateLoadingState();
        }
    }

    public void fetchAirQuality(String location) {
        try {
            Log.d(TAG, "Fetching air quality for location: " + location);
            RetrofitClient.getInstance().getWeatherApi().getAirQuality(location)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                try {
                                    String responseBody = response.body();
                                    JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

                                    if (jsonObject.has("code") && "200".equals(jsonObject.get("code").getAsString())) {
                                        JsonObject nowObject = jsonObject.getAsJsonObject("now");
                                        AirQuality air = gson.fromJson(nowObject, AirQuality.class);
                                        airQuality.postValue(air);
                                        weatherCache.saveAirQuality(air);
                                    } else {
                                        String code = jsonObject.has("code") ? jsonObject.get("code").getAsString()
                                                : "unknown";
                                        String message = jsonObject.has("message")
                                                ? jsonObject.get("message").getAsString()
                                                : "";
                                        errorMessage.postValue("API错误: " + code + " - " + message);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing air quality data", e);
                                    errorMessage.postValue("数据解析错误: " + e.getMessage());
                                }
                            } else {
                                Log.e(TAG, "Error response: " + response.code());
                                errorMessage.postValue("获取空气质量失败: " + response.code());
                            }
                            checkAndUpdateLoadingState();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e(TAG, "Network request failed", t);
                            errorMessage.postValue("网络请求失败: " + t.getMessage());
                            // 如果网络请求失败，尝试使用缓存数据
                            AirQuality cached = weatherCache.getAirQuality();
                            if (cached != null) {
                                airQuality.postValue(cached);
                            }
                            checkAndUpdateLoadingState();
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in fetchAirQuality", e);
            errorMessage.postValue("请求出错: " + e.getMessage());
            checkAndUpdateLoadingState();
        }
    }

    private void checkAndUpdateLoadingState() {
        int remaining = activeRequests.decrementAndGet();
        Log.d(TAG, "Remaining active requests: " + remaining);

        if (remaining <= 0) {
            Log.d(TAG, "All weather data requests completed");
            resetRequestState();
        }
    }

    // Getter方法
    public LiveData<WeatherNow> getCurrentWeather() {
        return currentWeather;
    }

    public LiveData<List<WeatherDaily>> getDailyForecast() {
        return dailyForecast;
    }

    public LiveData<List<WeatherHourly>> getHourlyForecast() {
        return hourlyForecast;
    }

    public LiveData<AirQuality> getAirQuality() {
        return airQuality;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}