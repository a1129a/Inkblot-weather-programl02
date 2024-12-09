package com.example.myapplication24;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication24.adapter.DailyForecastAdapter;
import com.example.myapplication24.adapter.HourlyForecastAdapter;
import com.example.myapplication24.databinding.ActivityMainBinding;
import com.example.myapplication24.model.AirQuality;
import com.example.myapplication24.model.City;
import com.example.myapplication24.model.WeatherDaily;
import com.example.myapplication24.model.WeatherHourly;
import com.example.myapplication24.model.WeatherNow;
import com.example.myapplication24.service.LocationService;
import com.example.myapplication24.viewmodel.CityViewModel;
import com.example.myapplication24.viewmodel.CitySearchViewModel;
import com.example.myapplication24.viewmodel.WeatherViewModel;
import com.example.myapplication24.utils.WeatherBackgroundUtils;

import java.util.List;

/**
 * 主活动类
 * 负责显示天气息和处理用户交互
 */
public class MainActivity extends AppCompatActivity implements LocationService.OnLocationResultListener {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private WeatherViewModel weatherViewModel;
    private CityViewModel cityViewModel;
    private DailyForecastAdapter dailyAdapter;
    private HourlyForecastAdapter hourlyAdapter;
    private LocationService locationService;
    private boolean isLocationRequested = false;
    private boolean isProcessingLocationUpdate = false;
    private static final long MIN_REFRESH_INTERVAL = 5 * 60 * 1000; // 5分钟刷新间隔
    private long lastRefreshTime = 0;
    private CitySearchViewModel citySearchViewModel;
    private final ActivityResultLauncher<Intent> cityManageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d(TAG, "City management activity result: " + result.getResultCode());
                if (result.getResultCode() == RESULT_OK) {
                    Log.d(TAG, "City selection changed from city management");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting MainActivity initialization");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModels();
        setupRecyclerViews();
        setupSwipeRefresh();
        setupLocationService();
        setupClickListeners();
        observeWeatherData();
        observeSelectedCity();

        checkSelectedCityOrRequestLocation();
        Log.d(TAG, "onCreate: MainActivity initialization completed");
    }

    private void initViewModels() {
        Log.d(TAG, "Initializing ViewModels");
        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        cityViewModel = new ViewModelProvider(this).get(CityViewModel.class);
        citySearchViewModel = new ViewModelProvider(this).get(CitySearchViewModel.class);
        Log.d(TAG, "ViewModels initialized successfully");
    }

    private void setupRecyclerViews() {
        Log.d(TAG, "Setting up RecyclerViews");
        dailyAdapter = new DailyForecastAdapter();
        binding.dailyForecastList.setAdapter(dailyAdapter);

        hourlyAdapter = new HourlyForecastAdapter();
        binding.hourlyForecastList.setAdapter(hourlyAdapter);
        Log.d(TAG, "RecyclerViews setup completed");
    }

    private void setupSwipeRefresh() {
        Log.d(TAG, "Setting up SwipeRefreshLayout");
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastRefreshTime < MIN_REFRESH_INTERVAL) {
                Log.d(TAG, "Refresh rejected: too frequent. Time since last refresh: " +
                        (currentTime - lastRefreshTime) + "ms");
                binding.swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(this, "请等待一段时间后再刷新", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "Manual refresh triggered");
            lastRefreshTime = currentTime;
            loadWeatherData();
        });
        Log.d(TAG, "SwipeRefreshLayout setup completed");
    }

    private void setupLocationService() {
        Log.d(TAG, "Setting up LocationService");
        locationService = new LocationService(this);
        Log.d(TAG, "LocationService setup completed");
    }

    private void setupClickListeners() {
        Log.d(TAG, "Setting up click listeners");

        binding.cityHeader.cityNameText.setOnClickListener(v -> {
            Log.d(TAG, "City name clicked, launching CityManageActivity");
            Intent intent = new Intent(this, CityManageActivity.class);
            cityManageLauncher.launch(intent);
        });

        binding.cityHeader.settingsButton.setOnClickListener(v -> {
            Log.d(TAG, "Settings button clicked, launching SettingsActivity");
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        binding.cityHeader.getRoot().setOnClickListener(v -> {
            Log.d(TAG, "City header clicked, launching CityManageActivity");
            Intent intent = new Intent(this, CityManageActivity.class);
            cityManageLauncher.launch(intent);
        });

        Log.d(TAG, "Click listeners setup completed");
    }

    private void observeWeatherData() {
        Log.d(TAG, "Setting up weather data observers");

        weatherViewModel.getIsLoading().observe(this, isLoading -> {
            Log.d(TAG, "Loading state changed: " + isLoading);
            binding.swipeRefreshLayout.setRefreshing(isLoading);
        });

        weatherViewModel.getCurrentWeather().observe(this, weather -> {
            Log.d(TAG, "Current weather data received: " + (weather != null));
            updateCurrentWeather(weather);
        });

        weatherViewModel.getDailyForecast().observe(this, forecasts -> {
            Log.d(TAG, "Daily forecast data received: " + (forecasts != null ? forecasts.size() : 0) + " items");
            updateDailyForecast(forecasts);
        });

        weatherViewModel.getHourlyForecast().observe(this, forecasts -> {
            Log.d(TAG, "Hourly forecast data received: " + (forecasts != null ? forecasts.size() : 0) + " items");
            updateHourlyForecast(forecasts);
        });

        weatherViewModel.getAirQuality().observe(this, airQuality -> {
            Log.d(TAG, "Air quality data received: " + (airQuality != null));
            updateAirQuality(airQuality);
        });

        weatherViewModel.getErrorMessage().observe(this, error -> {
            Log.e(TAG, "Error message received: " + error);
            showError(error);
        });

        Log.d(TAG, "Weather data observers setup completed");
    }

    private void observeSelectedCity() {
        Log.d(TAG, "Setting up selected city observer");
        cityViewModel.getSelectedCity().observe(this, city -> {
            if (city != null) {
                Log.d(TAG, "Selected city changed: " + city.getName() +
                        " (ID: " + city.getLocationId() + ")");
                updateUI(city);
                loadWeatherData(city.getLocationId());
            } else {
                Log.d(TAG, "No city selected, checking location request status");
                updateUI(null);
                if (!isLocationRequested) {
                    requestLocation();
                }
            }
        });
    }

    private void checkSelectedCityOrRequestLocation() {
        Log.d(TAG, "Checking selected city status");
        City selectedCity = cityViewModel.getSelectedCity().getValue();
        if (selectedCity == null && !isLocationRequested) {
            Log.d(TAG, "No selected city and no location requested, initiating location request");
            requestLocation();
        } else if (selectedCity != null) {
            Log.d(TAG, "Found selected city: " + selectedCity.getName());
        } else {
            Log.d(TAG, "Location request already in progress");
        }
    }

    private void requestLocation() {
        if (isLocationRequested) {
            Log.d(TAG, "Location request already in progress");
            return;
        }

        Log.d(TAG, "Initiating location request");
        isLocationRequested = true;
        binding.swipeRefreshLayout.setRefreshing(true);
        locationService.requestLocationUpdates(this);
    }

    private void loadWeatherData() {
        Log.d(TAG, "Loading weather data");
        City selectedCity = cityViewModel.getSelectedCity().getValue();
        if (selectedCity != null) {
            Log.d(TAG, "Loading weather data for selected city: " + selectedCity.getName());
            loadWeatherData(selectedCity.getLocationId());
        } else if (!isLocationRequested) {
            Log.d(TAG, "No selected city, requesting location");
            requestLocation();
        } else {
            Log.d(TAG, "Location request already in progress");
        }
    }

    private void loadWeatherData(String locationId) {
        if (locationId == null || locationId.isEmpty()) {
            Log.e(TAG, "Invalid location ID");
            binding.swipeRefreshLayout.setRefreshing(false);
            showError("无效的城市ID");
            return;
        }

        Log.d(TAG, "Loading weather data for location ID: " + locationId);
        binding.swipeRefreshLayout.setRefreshing(true);
        weatherViewModel.fetchWeatherData(locationId);
    }

    private void updateCurrentWeather(WeatherNow weather) {
        if (weather != null) {
            Log.d(TAG, String.format("Updating current weather - Temp: %s°, Condition: %s, " +
                    "Humidity: %s%%, Wind: %s级, Pressure: %shPa, Visibility: %skm",
                    weather.getTemp(), weather.getText(), weather.getHumidity(),
                    weather.getWindScale(), weather.getPressure(), weather.getVis()));

            binding.currentTempText.setText(weather.getTemp() + "°");
            binding.weatherConditionText.setText(weather.getText());
            binding.humidityText.setText("湿度 " + weather.getHumidity() + "%");
            binding.windSpeedText.setText("风力 " + weather.getWindScale() + "级");
            binding.pressureText.setText("气压 " + weather.getPressure() + "hPa");
            binding.visibilityText.setText("能见度 " + weather.getVis() + "km");

            WeatherBackgroundUtils.setWeatherBackground(binding.getRoot(), weather.getIcon());
            Log.d(TAG, "Weather background updated with icon: " + weather.getIcon());
        } else {
            Log.w(TAG, "Received null weather data");
        }
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    private void updateDailyForecast(List<WeatherDaily> forecasts) {
        if (forecasts != null && !forecasts.isEmpty()) {
            Log.d(TAG, "Updating daily forecast with " + forecasts.size() + " items");
            for (WeatherDaily forecast : forecasts) {
                Log.d(TAG, String.format("Daily forecast - Date: %s, Temp: %s°/%s°, Condition: %s",
                        forecast.getFxDate(), forecast.getTempMax(), forecast.getTempMin(),
                        forecast.getTextDay()));
            }
            dailyAdapter.updateForecasts(forecasts);
        } else {
            Log.w(TAG, "Received null or empty daily forecast");
        }
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    private void updateHourlyForecast(List<WeatherHourly> forecasts) {
        if (forecasts != null && !forecasts.isEmpty()) {
            Log.d(TAG, "Updating hourly forecast with " + forecasts.size() + " items");
            for (WeatherHourly forecast : forecasts) {
                Log.d(TAG, String.format("Hourly forecast - Time: %s, Temp: %s°, Condition: %s",
                        forecast.getFxTime(), forecast.getTemp(), forecast.getText()));
            }
            hourlyAdapter.updateForecasts(forecasts);
        } else {
            Log.w(TAG, "Received null or empty hourly forecast");
        }
    }

    private void updateAirQuality(AirQuality airQuality) {
        if (airQuality != null) {
            Log.d(TAG, String.format("Updating air quality - AQI: %s, Category: %s, PM2.5: %s",
                    airQuality.getAqi(), airQuality.getCategory(), airQuality.getPm2p5()));
            binding.aqiText.setText("空气质量 " + airQuality.getCategory());
            binding.pm25Text.setText("PM2.5: " + airQuality.getPm2p5());
        } else {
            Log.w(TAG, "Received null air quality data");
        }
    }

    private void showError(String message) {
        Log.e(TAG, "Error occurred: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLocationResult(Location location) {
        if (isProcessingLocationUpdate) {
            Log.d(TAG, "Location update already in progress, skipping");
            return;
        }

        try {
            isProcessingLocationUpdate = true;
            if (location != null) {
                Log.d(TAG, String.format("Location received - Lat: %.6f, Lon: %.6f, Accuracy: %.1fm",
                        location.getLatitude(), location.getLongitude(), location.getAccuracy()));

                // 停止位置更新
                locationService.stopLocationUpdates();

                citySearchViewModel.searchCityByLocation(location).observe(this, city -> {
                    if (city != null) {
                        Log.d(TAG, "Location search result: " + city.getName() +
                                " (ID: " + city.getLocationId() + ")");

                        // 设置城市信息
                        city.setSelected(true);
                        city.setLastUpdateTime(System.currentTimeMillis());
                        city.setLastAccessTime(System.currentTimeMillis());
                        city.setLatitude(String.valueOf(location.getLatitude()));
                        city.setLongitude(String.valueOf(location.getLongitude()));

                        // 保存到数据库
                        cityViewModel.insert(city);

                        // 直接更新UI和加载天气
                        updateUI(city);
                        loadWeatherData(city.getLocationId());
                    } else {
                        Log.w(TAG, "Location search returned no results");
                        showError("无法获取当前位置的城市信息");
                        binding.swipeRefreshLayout.setRefreshing(false);
                    }
                    isProcessingLocationUpdate = false;
                });
            } else {
                Log.w(TAG, "Received null location");
                showError("获取位置信息失败");
                binding.swipeRefreshLayout.setRefreshing(false);
                isProcessingLocationUpdate = false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing location update: " + e.getMessage());
            showError("处理位置信息时出错");
            binding.swipeRefreshLayout.setRefreshing(false);
            isProcessingLocationUpdate = false;
        }
    }

    @Override
    public void onLocationError(String error) {
        Log.e(TAG, "Location error occurred: " + error);
        showError("定位失败：" + error);
        isLocationRequested = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "Permission result received - Request code: " + requestCode);
        locationService.handlePermissionResult(requestCode, permissions, grantResults);
    }

    private void updateUI(City city) {
        if (city != null) {
            Log.d(TAG, String.format("Updating UI for city: %s (%s, %s)",
                    city.getName(), city.getLocationId(), city.getFullAddress()));
            binding.cityHeader.cityNameText.setText(city.getDistrict() != null ? city.getDistrict() : city.getName());
            binding.cityHeader.cityAddressText.setText(city.getFullAddress());
            binding.cityHeader.cityAddressText.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Updating UI for null city (default state)");
            binding.cityHeader.cityNameText.setText("选择城市");
            binding.cityHeader.cityAddressText.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        // 只在没有选中城市时请求位置
        City selectedCity = cityViewModel.getSelectedCity().getValue();
        if (selectedCity == null && !isLocationRequested) {
            requestLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
        // 停止位置更新
        locationService.stopLocationUpdates();
        isLocationRequested = false;
        isProcessingLocationUpdate = false;
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy called, cleaning up resources");
        super.onDestroy();
        locationService.stopLocationUpdates();
    }
}