package com.example.myapplication24.api;

/**
 * 地理位置服务API客户端
 * 用于城市搜索和地理编码服务
 */
public class GeoApiClient extends BaseApiClient {
    private static final String TAG = "GeoApiClient";
    private static final String BASE_URL = "https://geoapi.qweather.com/";
    private static GeoApiClient instance;

    private GeoApiClient() {
        super(BASE_URL, TAG);
    }

    public static synchronized GeoApiClient getInstance() {
        if (instance == null) {
            instance = new GeoApiClient();
        }
        return instance;
    }

    public GeoApi getGeoApi() {
        return retrofit.create(GeoApi.class);
    }
} 