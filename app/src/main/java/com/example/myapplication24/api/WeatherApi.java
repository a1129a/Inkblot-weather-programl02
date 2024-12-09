/**
 * WeatherApi.java
 * 版本: 2.0.0
 * 更新时间: 2024-01-10
 * 
 * 和风天气API接口定义类
 * 该类定义了与和风天气API通信所需的所有接口方法
 * 
 * 主要功能:
 * - 获取实时天气数据
 * - 获取未来7天天气预报
 * - 获取24小时天气预报
 * - 获取生活指数信息
 * - 获取空气质量数据
 * 
 * API版本: v7
 * 调用限制: 免费版每天1000次
 * 
 * 注意事项:
 * 1. 所有请求都需要API密钥
 * 2. 返回数据格式为JSON字符串
 * 3. 请注意API调用频率限制
 */

package com.example.myapplication24.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 和风天气API接口定义
 * 包含实时天气、每日天气预报、逐小时天气预报等接口
 */
public interface WeatherApi {
    /**
     * 获取实时天气信息
     * 
     * @param location 城市ID
     * @return 实时天气数据
     */
    @GET("v7/weather/now")
    Call<String> getNowWeather(@Query("location") String location);

    /**
     * 获取天气预报（未来7天）
     * 
     * @param location 城市ID
     * @return 天气预报数据
     */
    @GET("v7/weather/7d")
    Call<String> getDailyWeather(@Query("location") String location);

    /**
     * 获取逐小时预报（未来24小时）
     * 
     * @param location 城市ID
     * @return 逐小时预报数据
     */
    @GET("v7/weather/24h")
    Call<String> getHourlyWeather(@Query("location") String location);

    /**
     * 获取生活指数
     * 
     * @param location 城市ID
     * @param type     指数类型
     * @return 生活指数数据
     */
    @GET("v7/indices/1d")
    Call<String> getLifeIndices(@Query("location") String location, @Query("type") String type);

    /**
     * 获取空气质量
     * 
     * @param location 城市ID
     * @return 空气质量数据
     */
    @GET("v7/air/now")
    Call<String> getAirQuality(@Query("location") String location);
}