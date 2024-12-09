package com.example.myapplication24.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 城市数据模型
 * 用于存储城市信息和用户设置
 */
@Entity(tableName = "cities")
public class City {
    @PrimaryKey(autoGenerate = true)
    private int id;

    /**
     * 城市ID（和风天气API使用的ID）
     */
    private String locationId;

    /**
     * 城市名称
     */
    private String name;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区/县
     */
    private String district;

    /**
     * 是否是当前选中的城市
     */
    private boolean isSelected;

    /**
     * 是否是收藏的城市
     */
    private boolean isFavorite;

    /**
     * 最后更新时间
     */
    private long lastUpdateTime;

    /**
     * 最后访问时间（用于搜索历史排序）
     */
    private long lastAccessTime;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 行政区划代码
     */
    private String adCode;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    /**
     * 获取完整的地址显示
     * 
     * @return 省市区完整地址
     */
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (province != null && !province.isEmpty()) {
            address.append(province);
            if (city != null && !city.isEmpty() && !city.equals(province)) {
                address.append(" ").append(city);
            }
            if (district != null && !district.isEmpty()) {
                address.append(" ").append(district);
            }
        } else {
            address.append(name);
        }
        return address.toString();
    }
}