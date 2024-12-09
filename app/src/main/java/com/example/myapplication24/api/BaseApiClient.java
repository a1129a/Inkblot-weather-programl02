package com.example.myapplication24.api;

import android.util.Log;

import com.example.myapplication24.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * 基础HTTP客户端类
 * 提供通用的HTTP客户端配置
 */
public abstract class BaseApiClient {
    protected final Retrofit retrofit;

    protected BaseApiClient(String baseUrl, String tag) {
        try {
            // 创建日志拦截器
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
                    message -> Log.d(tag, "OkHttp: " + message));
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 配置OkHttpClient
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(chain -> {
                        // 添加API密钥到所有请求
                        okhttp3.Request original = chain.request();
                        okhttp3.HttpUrl url = original.url().newBuilder()
                                .addQueryParameter("key", BuildConfig.WEATHER_API_KEY)
                                .build();
                        okhttp3.Request request = original.newBuilder()
                                .url(url)
                                .build();
                        Log.d(tag, "Sending request: " + request.url());
                        return chain.proceed(request);
                    })
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();

            // 创建Retrofit实例
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

            Log.d(tag, "ApiClient initialized successfully");
        } catch (Exception e) {
            Log.e(tag, "Error initializing ApiClient", e);
            throw new RuntimeException("ApiClient initialization failed", e);
        }
    }
}