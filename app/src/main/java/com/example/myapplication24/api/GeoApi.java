package com.example.myapplication24.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 和风天气地理位置服务API接口
 * 用于城市搜索和地理编码服务
 */
public interface GeoApi {
    /**
     * 城市搜索
     */
    @GET("v2/city/lookup")
    Call<String> searchCity(@Query("location") String location);

    /**
     * 根据经纬度获取城市信息
     */
    @GET("v2/city/lookup")
    Call<String> getCityByLocation(@Query("location") String location);
}