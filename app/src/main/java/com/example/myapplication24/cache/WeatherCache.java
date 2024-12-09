/**
 * WeatherCache.java
 * 版本: 2.0.0
 * 更新时间: 2024-01-10
 * 
 * 天气数据缓存管理类
 * 使用SharedPreferences实现数据的本地缓存
 * 
 * 主要功能:
 * - 缓存实时天气数据
 * - 缓存天气预报数据
 * - 缓存空气质量数据
 * - 管理缓存有效期
 * 
 * 缓存策略:
 * 1. 缓存时间: 30分钟
 * 2. 使用Gson序列化对象
 * 3. 支持增量更新
 * 
 * 优化说明:
 * 1. 减少API调用频率
 * 2. 提升应用响应速度
 * 3. 优化离线体验
 * 
 * 使用方式:
 * WeatherCache cache = new WeatherCache(context);
 * cache.saveNowWeather(weather);
 * WeatherNow weather = cache.getNowWeather();
 */

package com.example.myapplication24.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.example.myapplication24.model.WeatherNow;
import com.example.myapplication24.model.WeatherDaily;
import com.example.myapplication24.model.WeatherHourly;
import com.example.myapplication24.model.AirQuality;

import java.util.List;

public class WeatherCache {
    private static final String PREF_NAME = "weather_cache";
    private static final String KEY_NOW_WEATHER = "now_weather";
    private static final String KEY_DAILY_WEATHER = "daily_weather";
    private static final String KEY_HOURLY_WEATHER = "hourly_weather";
    private static final String KEY_AIR_QUALITY = "air_quality";
    private static final String KEY_LAST_UPDATE_TIME = "last_update_time";

    private static final long CACHE_DURATION = 30 * 60 * 1000; // 30分钟缓存时间

    private final SharedPreferences preferences;
    private final Gson gson;

    public WeatherCache(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveNowWeather(WeatherNow weather) {
        preferences.edit()
                .putString(KEY_NOW_WEATHER, gson.toJson(weather))
                .putLong(KEY_LAST_UPDATE_TIME, System.currentTimeMillis())
                .apply();
    }

    public WeatherNow getNowWeather() {
        String json = preferences.getString(KEY_NOW_WEATHER, null);
        return json != null ? gson.fromJson(json, WeatherNow.class) : null;
    }

    public void saveDailyWeather(List<WeatherDaily> weatherList) {
        preferences.edit()
                .putString(KEY_DAILY_WEATHER, gson.toJson(weatherList))
                .apply();
    }

    public List<WeatherDaily> getDailyWeather() {
        String json = preferences.getString(KEY_DAILY_WEATHER, null);
        if (json == null)
            return null;
        return gson.fromJson(json, new com.google.gson.reflect.TypeToken<List<WeatherDaily>>() {
        }.getType());
    }

    public void saveHourlyWeather(List<WeatherHourly> weatherList) {
        preferences.edit()
                .putString(KEY_HOURLY_WEATHER, gson.toJson(weatherList))
                .apply();
    }

    public List<WeatherHourly> getHourlyWeather() {
        String json = preferences.getString(KEY_HOURLY_WEATHER, null);
        if (json == null)
            return null;
        return gson.fromJson(json, new com.google.gson.reflect.TypeToken<List<WeatherHourly>>() {
        }.getType());
    }

    public void saveAirQuality(AirQuality airQuality) {
        preferences.edit()
                .putString(KEY_AIR_QUALITY, gson.toJson(airQuality))
                .apply();
    }

    public AirQuality getAirQuality() {
        String json = preferences.getString(KEY_AIR_QUALITY, null);
        return json != null ? gson.fromJson(json, AirQuality.class) : null;
    }

    public boolean isCacheValid() {
        long lastUpdateTime = preferences.getLong(KEY_LAST_UPDATE_TIME, 0);
        return System.currentTimeMillis() - lastUpdateTime < CACHE_DURATION;
    }

    public void clearCache() {
        preferences.edit().clear().apply();
    }
}