package com.example.myapplication24;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "WeatherAppPrefs";
    private static final String PREF_THEME = "app_theme";
    private static final String PREF_NOTIFICATION = "notification_enabled";
    private static final String PREF_ALERT = "alert_enabled";

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 初始化SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // 设置工具栏
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 初始化主题设置
        setupThemeSettings();

        // 初始化通知设置
        setupNotificationSettings();

        // 设置版本号
        setupVersionInfo();
    }

    private void setupThemeSettings() {
        RadioGroup themeGroup = findViewById(R.id.themeGroup);
        RadioButton themeDefault = findViewById(R.id.themeDefault);
        RadioButton themeDark = findViewById(R.id.themeDark);
        RadioButton themeLight = findViewById(R.id.themeLight);

        // 设置当前主题
        int currentTheme = prefs.getInt(PREF_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        switch (currentTheme) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                themeLight.setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                themeDark.setChecked(true);
                break;
            default:
                themeDefault.setChecked(true);
                break;
        }

        // 监听主题切换
        themeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int theme;
            if (checkedId == R.id.themeLight) {
                theme = AppCompatDelegate.MODE_NIGHT_NO;
            } else if (checkedId == R.id.themeDark) {
                theme = AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                theme = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            }

            // 保存设置
            prefs.edit().putInt(PREF_THEME, theme).apply();

            // 应用主题
            AppCompatDelegate.setDefaultNightMode(theme);
        });
    }

    private void setupNotificationSettings() {
        SwitchMaterial notificationSwitch = findViewById(R.id.notificationSwitch);
        SwitchMaterial alertSwitch = findViewById(R.id.alertSwitch);

        // 设置当前状态
        notificationSwitch.setChecked(prefs.getBoolean(PREF_NOTIFICATION, true));
        alertSwitch.setChecked(prefs.getBoolean(PREF_ALERT, true));

        // 监听开关变化
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(PREF_NOTIFICATION, isChecked).apply();
            // TODO: 更新通知服务状态
        });

        alertSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(PREF_ALERT, isChecked).apply();
            // TODO: 更新预警服务状态
        });
    }

    private void setupVersionInfo() {
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            findViewById(R.id.versionText).setOnClickListener(v -> {
                // TODO: 添加版本检查更新功能
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}