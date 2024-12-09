package com.example.myapplication24.model;

/**
 * 每日天气预报数据模型
 * 对应和风天气API的每日天气预报数据结构
 */
public class WeatherDaily {
    /**
     * 预报日期
     */
    private String fxDate;

    /**
     * 日出时间
     */
    private String sunrise;

    /**
     * 日落时间
     */
    private String sunset;

    /**
     * 月升时间
     */
    private String moonrise;

    /**
     * 月落时间
     */
    private String moonset;

    /**
     * 月相名称
     */
    private String moonPhase;

    /**
     * 最高温度
     */
    private String tempMax;

    /**
     * 最低温度
     */
    private String tempMin;

    /**
     * 白天天气状况图标代码
     */
    private String iconDay;

    /**
     * 白天天气状况文字描述
     */
    private String textDay;

    /**
     * 夜间天气状况图标代码
     */
    private String iconNight;

    /**
     * 夜间天气状况文字描述
     */
    private String textNight;

    /**
     * 白天风向360���度
     */
    private String wind360Day;

    /**
     * 白天风向
     */
    private String windDirDay;

    /**
     * 白天风力等级
     */
    private String windScaleDay;

    /**
     * 白天风速
     */
    private String windSpeedDay;

    /**
     * 相对湿度
     */
    private String humidity;

    /**
     * 预计降水量
     */
    private String precip;

    /**
     * 紫外线强度指数
     */
    private String uvIndex;

    // getter和setter方法
    public String getFxDate() {
        return fxDate;
    }

    public void setFxDate(String fxDate) {
        this.fxDate = fxDate;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getMoonrise() {
        return moonrise;
    }

    public void setMoonrise(String moonrise) {
        this.moonrise = moonrise;
    }

    public String getMoonset() {
        return moonset;
    }

    public void setMoonset(String moonset) {
        this.moonset = moonset;
    }

    public String getMoonPhase() {
        return moonPhase;
    }

    public void setMoonPhase(String moonPhase) {
        this.moonPhase = moonPhase;
    }

    public String getTempMax() {
        return tempMax;
    }

    public void setTempMax(String tempMax) {
        this.tempMax = tempMax;
    }

    public String getTempMin() {
        return tempMin;
    }

    public void setTempMin(String tempMin) {
        this.tempMin = tempMin;
    }

    public String getIconDay() {
        return iconDay;
    }

    public void setIconDay(String iconDay) {
        this.iconDay = iconDay;
    }

    public String getTextDay() {
        return textDay;
    }

    public void setTextDay(String textDay) {
        this.textDay = textDay;
    }

    public String getIconNight() {
        return iconNight;
    }

    public void setIconNight(String iconNight) {
        this.iconNight = iconNight;
    }

    public String getTextNight() {
        return textNight;
    }

    public void setTextNight(String textNight) {
        this.textNight = textNight;
    }

    public String getWind360Day() {
        return wind360Day;
    }

    public void setWind360Day(String wind360Day) {
        this.wind360Day = wind360Day;
    }

    public String getWindDirDay() {
        return windDirDay;
    }

    public void setWindDirDay(String windDirDay) {
        this.windDirDay = windDirDay;
    }

    public String getWindScaleDay() {
        return windScaleDay;
    }

    public void setWindScaleDay(String windScaleDay) {
        this.windScaleDay = windScaleDay;
    }

    public String getWindSpeedDay() {
        return windSpeedDay;
    }

    public void setWindSpeedDay(String windSpeedDay) {
        this.windSpeedDay = windSpeedDay;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPrecip() {
        return precip;
    }

    public void setPrecip(String precip) {
        this.precip = precip;
    }

    public String getUvIndex() {
        return uvIndex;
    }

    public void setUvIndex(String uvIndex) {
        this.uvIndex = uvIndex;
    }
}