package com.example.myapplication24.viewmodel;

import android.app.Application;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication24.model.City;
import com.example.myapplication24.repository.CitySearchRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 城市搜索ViewModel
 * 用于管理城市搜索的业务逻辑
 */
public class CitySearchViewModel extends AndroidViewModel {
    private final CitySearchRepository repository;
    private final AtomicBoolean isSearching = new AtomicBoolean(false);
    private final AtomicBoolean isLocationSearching = new AtomicBoolean(false);
    private static final long SEARCH_DEBOUNCE_TIME = 500; // 500ms防抖时间
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private String lastSearchKeyword;
    private Location lastSearchLocation;
    private static final long LOCATION_SEARCH_COOLDOWN = 5000; // 5秒定位搜索冷却时间
    private long lastLocationSearchTime = 0;

    public CitySearchViewModel(@NonNull Application application) {
        super(application);
        repository = new CitySearchRepository(application);
    }

    /**
     * 搜索城市
     * 
     * @param keyword 搜索关键词
     * @return 搜索结果LiveData
     */
    public LiveData<List<City>> searchCities(String keyword) {
        if (keyword == null || keyword.trim().isEmpty() ||
                (keyword.equals(lastSearchKeyword) && isSearching.get())) {
            return new MutableLiveData<>();
        }

        MutableLiveData<List<City>> result = new MutableLiveData<>();

        // 取消之前的搜索
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }

        // 创建新的搜索任务
        searchRunnable = () -> {
            if (isSearching.compareAndSet(false, true)) {
                try {
                    lastSearchKeyword = keyword;
                    repository.searchCities(keyword).observeForever(cities -> {
                        result.postValue(cities);
                        isSearching.set(false);
                    });
                } catch (Exception e) {
                    isSearching.set(false);
                    result.postValue(null);
                }
            }
        };

        // 延迟执行搜索，实现防抖
        searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_TIME);

        return result;
    }

    /**
     * 根据经纬度获取城市信息
     * 
     * @param location 位置信息
     * @return 城市信息LiveData
     */
    public LiveData<City> searchCityByLocation(Location location) {
        if (location == null || isLocationSearching.get()) {
            return new MutableLiveData<>();
        }

        // 检查定位搜索冷却时间
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastLocationSearchTime < LOCATION_SEARCH_COOLDOWN) {
            return new MutableLiveData<>();
        }

        // 检查位置是否有显著变化
        if (lastSearchLocation != null &&
                location.distanceTo(lastSearchLocation) < 100) { // 小于100米不更新
            return new MutableLiveData<>();
        }

        MutableLiveData<City> result = new MutableLiveData<>();

        if (isLocationSearching.compareAndSet(false, true)) {
            try {
                lastSearchLocation = location;
                lastLocationSearchTime = currentTime;
                repository.getCityByLocation(location).observeForever(city -> {
                    result.postValue(city);
                    isLocationSearching.set(false);
                });
            } catch (Exception e) {
                isLocationSearching.set(false);
                result.postValue(null);
            }
        }

        return result;
    }
}