package com.example.myapplication24;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class WeatherApplication extends Application {
    private static final String PREFS_NAME = "WeatherAppPrefs";
    private static final String PREF_THEME = "app_theme";

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化主题设置
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int theme = prefs.getInt(PREF_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(theme);
    }
}