package com.example.myapplication24.repository;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication24.BuildConfig;
import com.example.myapplication24.api.GeoApi;
import com.example.myapplication24.api.GeoApiClient;
import com.example.myapplication24.model.City;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 城市搜索仓库
 * 用于处理城市搜索和定位相关的业务逻辑
 */
public class CitySearchRepository {
    private static final String TAG = "CitySearchRepository";
    private final GeoApi geoApi;
    private final CityRepository cityRepository;
    private final String apiKey;

    public CitySearchRepository(Application application) {
        geoApi = GeoApiClient.getInstance().getGeoApi();
        cityRepository = new CityRepository(application);
        apiKey = BuildConfig.WEATHER_API_KEY;
    }

    /**
     * 搜索城市
     * 
     * @param keyword 搜索关键词
     * @return 搜索结果LiveData
     */
    public LiveData<List<City>> searchCities(String keyword) {
        MutableLiveData<List<City>> result = new MutableLiveData<>();

        geoApi.searchCity(keyword).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        List<City> cities = parseCityResponse(response.body());
                        result.postValue(cities);
                    } else {
                        Log.e(TAG, "Search city failed: " + response.errorBody());
                        result.postValue(new ArrayList<>());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Parse city response failed", e);
                    result.postValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Search city request failed", t);
                result.postValue(new ArrayList<>());
            }
        });

        return result;
    }

    /**
     * 根据纬度获取城市信息
     * 
     * @param location 位置信息
     * @return 城市信息LiveData
     */
    public LiveData<City> getCityByLocation(Location location) {
        MutableLiveData<City> result = new MutableLiveData<>();
        String locationString = location.getLongitude() + "," + location.getLatitude();

        geoApi.getCityByLocation(locationString).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        List<City> cities = parseCityResponse(response.body());
                        if (!cities.isEmpty()) {
                            City city = cities.get(0);
                            city.setSelected(true);
                            cityRepository.insert(city);
                            result.postValue(city);
                        } else {
                            result.postValue(null);
                        }
                    } else {
                        Log.e(TAG, "Get city by location failed: " + response.errorBody());
                        result.postValue(null);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Parse city response failed", e);
                    result.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Get city by location request failed", t);
                result.postValue(null);
            }
        });

        return result;
    }

    /**
     * 解析城市搜索响应
     * 
     * @param response API响应
     * @return 城市列表
     */
    private List<City> parseCityResponse(String response) throws Exception {
        List<City> cities = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(response);

        // 检查返回码
        String code = jsonObject.getString("code");
        if (!"200".equals(code)) {
            String message = jsonObject.optString("message", "未知错误");
            throw new Exception("API错误: " + code + " - " + message);
        }

        // 只有返回成功时才解析location数据
        JSONArray locations = jsonObject.getJSONArray("location");
        for (int i = 0; i < locations.length(); i++) {
            JSONObject location = locations.getJSONObject(i);
            City city = new City();
            city.setLocationId(location.getString("id"));
            city.setName(location.getString("name"));
            city.setLatitude(location.getString("lat"));
            city.setLongitude(location.getString("lon"));

            // 设置省市区信息
            city.setProvince(location.optString("adm1", ""));
            city.setCity(location.optString("adm2", ""));
            city.setDistrict(location.getString("name"));

            city.setLastUpdateTime(System.currentTimeMillis());
            city.setLastAccessTime(System.currentTimeMillis());
            cities.add(city);
        }

        return cities;
    }
}