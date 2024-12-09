package com.example.myapplication24.model;

/**
 * 小时天气预报数据模型
 * 对应和风天气API的小时天气预报数据结构
 */
public class WeatherHourly {
    /**
     * 预报时间
     */
    private String fxTime;

    /**
     * 温度
     */
    private String temp;

    /**
     * 天气状况图标代码
     */
    private String icon;

    /**
     * 天气状况文字描述
     */
    private String text;

    /**
     * 风向360角度
     */
    private String wind360;

    /**
     * 风向
     */
    private String windDir;

    /**
     * 风力等级
     */
    private String windScale;

    /**
     * 风速，公里/小时
     */
    private String windSpeed;

    /**
     * 相对湿度，百分比
     */
    private String humidity;

    /**
     * 当前小时累计降水量，毫米
     */
    private String precip;

    /**
     * 大气压强，百帕
     */
    private String pressure;

    // getter和setter方法
    public String getFxTime() {
        return fxTime;
    }

    public void setFxTime(String fxTime) {
        this.fxTime = fxTime;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getWind360() {
        return wind360;
    }

    public void setWind360(String wind360) {
        this.wind360 = wind360;
    }

    public String getWindDir() {
        return windDir;
    }

    public void setWindDir(String windDir) {
        this.windDir = windDir;
    }

    public String getWindScale() {
        return windScale;
    }

    public void setWindScale(String windScale) {
        this.windScale = windScale;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
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

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }
}