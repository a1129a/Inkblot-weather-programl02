package com.example.myapplication24.model;

/**
 * 空气质量数据模型
 * 对应和风天气API的空气质量数据结构
 */
public class AirQuality {
    /**
     * 空气质量指数
     */
    private String aqi;

    /**
     * 空气质量指数级别
     */
    private String level;

    /**
     * 空气质量指数级别说明
     */
    private String category;

    /**
     * 空气质量的主要污染物
     */
    private String primary;

    /**
     * PM2.5
     */
    private String pm2p5;

    /**
     * PM10
     */
    private String pm10;

    /**
     * 二氧化氮
     */
    private String no2;

    /**
     * 二氧化硫
     */
    private String so2;

    /**
     * 一氧化碳
     */
    private String co;

    /**
     * 臭氧
     */
    private String o3;

    // getter和setter方法
    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getPm2p5() {
        return pm2p5;
    }

    public void setPm2p5(String pm2p5) {
        this.pm2p5 = pm2p5;
    }

    public String getPm10() {
        return pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10;
    }

    public String getNo2() {
        return no2;
    }

    public void setNo2(String no2) {
        this.no2 = no2;
    }

    public String getSo2() {
        return so2;
    }

    public void setSo2(String so2) {
        this.so2 = so2;
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getO3() {
        return o3;
    }

    public void setO3(String o3) {
        this.o3 = o3;
    }
}