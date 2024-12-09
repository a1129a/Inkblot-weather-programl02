package com.example.myapplication24.utils;

import android.graphics.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * 天气图标工具类
 * 使用emoji表情显示天气状况
 */
public class WeatherIconUtils {
    // 天气图标映射表
    private static final Map<String, String> weatherEmojis = new HashMap<>();
    private static final Map<String, Integer> weatherColors = new HashMap<>();

    /**
     * 初始化天气图标映射
     */
    static {
        // 晴天相关
        weatherEmojis.put("100", "☀️"); // 晴
        weatherEmojis.put("101", "🌤️"); // 多云
        weatherEmojis.put("102", "⛅"); // 少云
        weatherEmojis.put("103", "🌥️"); // 晴间多云
        weatherEmojis.put("104", "☁️"); // 阴

        // 雨相关
        weatherEmojis.put("300", "🌦️"); // 阵雨
        weatherEmojis.put("301", "🌧️"); // 强阵雨
        weatherEmojis.put("302", "⛈️"); // 雷阵雨
        weatherEmojis.put("303", "⛈️"); // 强雷阵雨
        weatherEmojis.put("304", "⛈️"); // 雷阵雨伴有冰雹
        weatherEmojis.put("305", "🌧️"); // 小雨
        weatherEmojis.put("306", "🌧️"); // 中雨
        weatherEmojis.put("307", "🌧️"); // 大雨
        weatherEmojis.put("308", "🌧️"); // 极端降雨
        weatherEmojis.put("309", "🌧️"); // 毛毛雨/细雨
        weatherEmojis.put("310", "🌧️"); // 暴雨
        weatherEmojis.put("311", "🌧️"); // 大暴雨
        weatherEmojis.put("312", "🌧️"); // 特大暴雨
        weatherEmojis.put("313", "🌨️"); // 冻雨
        weatherEmojis.put("314", "🌧️"); // 小到中雨
        weatherEmojis.put("315", "🌧️"); // 中到大雨
        weatherEmojis.put("316", "🌧️"); // 大到暴雨
        weatherEmojis.put("317", "🌧️"); // 暴雨到大暴雨
        weatherEmojis.put("318", "🌧️"); // 大暴雨到特大暴雨

        // 雪相关
        weatherEmojis.put("400", "🌨️"); // 小雪
        weatherEmojis.put("401", "🌨️"); // 中雪
        weatherEmojis.put("402", "🌨️"); // 大雪
        weatherEmojis.put("403", "🌨️"); // 暴雪
        weatherEmojis.put("404", "🌨️"); // 雨夹雪
        weatherEmojis.put("405", "🌨️"); // 雨雪天气
        weatherEmojis.put("406", "🌨️"); // 阵雨夹雪
        weatherEmojis.put("407", "🌨️"); // 阵雪

        // 特殊天气
        weatherEmojis.put("500", "🌫️"); // 薄雾
        weatherEmojis.put("501", "🌫️"); // 雾
        weatherEmojis.put("502", "🌫️"); // 霾
        weatherEmojis.put("503", "🌪️"); // 扬沙
        weatherEmojis.put("504", "🌪️"); // 浮尘
        weatherEmojis.put("507", "🌪️"); // 沙尘暴
        weatherEmojis.put("508", "🌪️"); // 强沙尘暴
        weatherEmojis.put("509", "🌫️"); // 浓雾
        weatherEmojis.put("510", "🌫️"); // 强浓雾
        weatherEmojis.put("511", "🌫️"); // 中度霾
        weatherEmojis.put("512", "🌫️"); // 重度霾
        weatherEmojis.put("513", "🌫️"); // 严重霾
        weatherEmojis.put("514", "🌪️"); // 大风
        weatherEmojis.put("515", "🌪️"); // 龙卷风

        // 夜间天气
        weatherEmojis.put("150", "🌙"); // 晴
        weatherEmojis.put("151", "🌤️"); // 多云
        weatherEmojis.put("152", "⛅"); // 少云
        weatherEmojis.put("153", "🌥️"); // 晴间多云

        // 天气颜色
        weatherColors.put("100", Color.parseColor("#FF9800")); // 晴天 - 橙色
        weatherColors.put("101", Color.parseColor("#78909C")); // 多云 - 灰蓝色
        weatherColors.put("104", Color.parseColor("#607D8B")); // 阴天 - 深灰蓝色
        weatherColors.put("300", Color.parseColor("#42A5F5")); // 雨天 - 蓝色
        weatherColors.put("400", Color.parseColor("#90CAF9")); // 雪天 - 浅蓝色
        weatherColors.put("500", Color.parseColor("#B0BEC5")); // 雾天 - 灰色
    }

    /**
     * 获取天气emoji
     * 
     * @param code 和风天气图标代码
     * @return 对应的emoji表情，如果没有找到对应的emoji则返回默认表情
     */
    public static String getWeatherEmoji(String code) {
        return weatherEmojis.getOrDefault(code, "❓");
    }

    /**
     * 获取天气主题色
     * 
     * @param code 和风天气图标代码
     * @return 对应的颜色值
     */
    public static int getWeatherColor(String code) {
        // 如果找不到对应的颜色，返回默认颜色（深蓝色）
        return weatherColors.getOrDefault(code, Color.parseColor("#1976D2"));
    }

    /**
     * 判断是否是夜间图标代码
     * 
     * @param code 和风天气图标代码
     * @return 是否是夜间图标
     */
    public static boolean isNightIcon(String code) {
        return code != null && code.startsWith("15");
    }
}