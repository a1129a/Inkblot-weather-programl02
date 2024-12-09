/**
 * RetrofitClient.java
 * 版本: 2.0.0
 * 更新时间: 2024-01-10
 * 
 * Retrofit HTTP客户端封装类
 * 使用单例模式确保全局只有一个Retrofit实例
 * 
 * 主要功能:
 * - 创建和管理Retrofit实例
 * - 提供WeatherApi接口实现
 * - 处理API请求配置
 * 
 * 技术特点:
 * 1. 单例模式确保实例唯一性
 * 2. 继承BaseApiClient获取通用配置
 * 3. 线程安全的实例化方式
 * 
 * 使用方式:
 * WeatherApi api = RetrofitClient.getInstance().getWeatherApi();
 */

package com.example.myapplication24.api;

/**
 * 天气API客户端
 * 使用单例模式确保全局只有一个实例
 */
public class RetrofitClient extends BaseApiClient {
    private static final String TAG = "RetrofitClient";
    private static final String BASE_URL = "https://devapi.qweather.com/";
    private static RetrofitClient instance;

    private RetrofitClient() {
        super(BASE_URL, TAG);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public WeatherApi getWeatherApi() {
        return retrofit.create(WeatherApi.class);
    }
}