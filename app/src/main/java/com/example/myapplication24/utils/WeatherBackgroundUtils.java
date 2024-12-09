package com.example.myapplication24.utils;

import android.view.View;
import java.util.Calendar;

import com.example.myapplication24.R;

/**
 * 天气背景工具类
 * 用于根据天气状况和时间设置合适的背景
 */
public class WeatherBackgroundUtils {

    /**
     * 设置天气背景
     * 
     * @param view        需要设置背景的视图
     * @param weatherCode 天气代码
     */
    public static void setWeatherBackground(View view, String weatherCode) {
        // 获取当前小时
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        // 如果是夜间（18:00-06:00），使用夜间背景
        if (hour >= 18 || hour < 6) {
            view.setBackgroundResource(R.drawable.weather_background_night);
            return;
        }

        // 根据天气代码设置背景
        if (weatherCode != null) {
            if (isSunny(weatherCode)) {
                view.setBackgroundResource(R.drawable.weather_background_sunny);
            } else if (isCloudy(weatherCode)) {
                view.setBackgroundResource(R.drawable.weather_background_cloudy);
            } else if (isRainy(weatherCode)) {
                view.setBackgroundResource(R.drawable.weather_background_rainy);
            } else {
                // 默认使用晴天背景
                view.setBackgroundResource(R.drawable.weather_background_sunny);
            }
        } else {
            // 如果没有天气代码，使用默认背景
            view.setBackgroundResource(R.drawable.weather_background_sunny);
        }
    }

    /**
     * 判断是否是晴天
     */
    private static boolean isSunny(String code) {
        return code.equals("100") || code.equals("150");
    }

    /**
     * 判断是否是多云
     */
    private static boolean isCloudy(String code) {
        return code.startsWith("101") || code.startsWith("102") || code.startsWith("103");
    }

    /**
     * 判断是否是雨天
     */
    private static boolean isRainy(String code) {
        return code.startsWith("300") || code.startsWith("301") || code.startsWith("302") ||
                code.startsWith("303") || code.startsWith("304") || code.startsWith("305") ||
                code.startsWith("306") || code.startsWith("307") || code.startsWith("308") ||
                code.startsWith("309") || code.startsWith("310") || code.startsWith("311") ||
                code.startsWith("312") || code.startsWith("313");
    }
}