package com.example.myapplication24.repository;

import android.app.Application;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.myapplication24.db.CityDao;
import com.example.myapplication24.db.WeatherDatabase;
import com.example.myapplication24.model.City;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 城市管理仓库
 * 用于管理城市数据的增删改查和定位服务
 */
public class CityRepository {
    private final CityDao cityDao;
    private final ExecutorService executorService;
    private static final int RECENT_CITIES_LIMIT = 10;

    public CityRepository(Application application) {
        WeatherDatabase database = WeatherDatabase.getInstance(application);
        cityDao = database.cityDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * 插入城市
     */
    public void insert(City city) {
        executorService.execute(() -> cityDao.insert(city));
    }

    /**
     * 删除城市
     */
    public void delete(City city) {
        executorService.execute(() -> cityDao.delete(city));
    }

    /**
     * 更新城市
     */
    public void update(City city) {
        executorService.execute(() -> cityDao.update(city));
    }

    /**
     * 获取所有城市
     */
    public LiveData<List<City>> getAllCities() {
        return cityDao.getAllCities();
    }

    /**
     * 获取收藏的城市
     */
    public LiveData<List<City>> getFavoriteCities() {
        return cityDao.getFavoriteCities();
    }

    /**
     * 获取当前选中的城市
     */
    public LiveData<City> getSelectedCity() {
        return cityDao.getSelectedCity();
    }

    /**
     * 获取最近访问的城市
     */
    public LiveData<List<City>> getRecentCities() {
        return cityDao.getRecentCities(RECENT_CITIES_LIMIT);
    }

    /**
     * 搜索城市
     */
    public LiveData<List<City>> searchCities(String keyword) {
        return cityDao.searchCities(keyword);
    }

    /**
     * 清除所有城市的选中状态
     */
    public void clearSelectedCity(Runnable onComplete) {
        executorService.execute(() -> {
            cityDao.clearSelectedCity();
            if (onComplete != null) {
                new Handler(Looper.getMainLooper()).post(onComplete);
            }
        });
    }

    /**
     * 设置选中的城市
     */
    public void setSelectedCity(City city) {
        executorService.execute(() -> {
            cityDao.clearSelectedCity();
            city.setSelected(true);
            cityDao.update(city);
            updateAccessTime(city);
        });
    }

    /**
     * 更新城市访问时间
     */
    public void updateAccessTime(City city) {
        executorService.execute(() -> cityDao.updateAccessTime(city.getId(), System.currentTimeMillis()));
    }

    /**
     * 切换城市收藏状态
     */
    public void toggleFavorite(City city) {
        city.setFavorite(!city.isFavorite());
        update(city);
    }

    /**
     * 根据定位信息获取或创建城市
     */
    public void getOrCreateCityByLocation(Location location, String cityName, String locationId,
            OnCityLoadedCallback callback) {
        executorService.execute(() -> {
            City city = cityDao.getCityByLocationId(locationId);
            if (city == null) {
                city = new City();
                city.setLocationId(locationId);
                city.setName(cityName);
                city.setLatitude(String.valueOf(location.getLatitude()));
                city.setLongitude(String.valueOf(location.getLongitude()));
                city.setLastUpdateTime(System.currentTimeMillis());
                city.setLastAccessTime(System.currentTimeMillis());
                cityDao.insert(city);
            }
            callback.onCityLoaded(city);
        });
    }

    /**
     * 城市加载回调接口
     */
    public interface OnCityLoadedCallback {
        void onCityLoaded(City city);
    }
}