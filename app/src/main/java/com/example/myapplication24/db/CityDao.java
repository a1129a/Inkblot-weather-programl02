package com.example.myapplication24.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication24.model.City;

import java.util.List;

/**
 * 城市数据库访问对象
 * 用于管理城市数据的增删改查
 */
@Dao
public interface CityDao {
    /**
     * 插入城市
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(City city);

    /**
     * 删除城市
     */
    @Delete
    void delete(City city);

    /**
     * 更新城市
     */
    @Update
    void update(City city);

    /**
     * 获取所有城市
     */
    @Query("SELECT * FROM cities ORDER BY lastAccessTime DESC")
    LiveData<List<City>> getAllCities();

    /**
     * 获取收藏的城市
     */
    @Query("SELECT * FROM cities WHERE isFavorite = 1 ORDER BY lastAccessTime DESC")
    LiveData<List<City>> getFavoriteCities();

    /**
     * 获取当前选中的城市
     */
    @Query("SELECT * FROM cities WHERE isSelected = 1 LIMIT 1")
    LiveData<City> getSelectedCity();

    /**
     * 获取最近访问的城市（历史记录）
     */
    @Query("SELECT * FROM cities ORDER BY lastAccessTime DESC LIMIT :limit")
    LiveData<List<City>> getRecentCities(int limit);

    /**
     * 搜索城市
     */
    @Query("SELECT * FROM cities WHERE name LIKE '%' || :keyword || '%' ORDER BY lastAccessTime DESC")
    LiveData<List<City>> searchCities(String keyword);

    /**
     * 根据locationId查找城市
     */
    @Query("SELECT * FROM cities WHERE locationId = :locationId LIMIT 1")
    City getCityByLocationId(String locationId);

    /**
     * 取消所有城市的选中状态
     */
    @Query("UPDATE cities SET isSelected = 0")
    void clearSelectedCity();

    /**
     * 设置城市为选中状态
     */
    @Query("UPDATE cities SET isSelected = 1 WHERE id = :cityId")
    void setSelectedCity(int cityId);

    /**
     * 更新城市访问时间
     */
    @Query("UPDATE cities SET lastAccessTime = :timestamp WHERE id = :cityId")
    void updateAccessTime(int cityId, long timestamp);
}