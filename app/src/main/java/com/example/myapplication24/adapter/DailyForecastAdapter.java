package com.example.myapplication24.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication24.R;
import com.example.myapplication24.model.WeatherDaily;
import com.example.myapplication24.utils.WeatherIconUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 每日天气预报适配器
 * 用于显示7天天气预报列表
 */
public class DailyForecastAdapter extends RecyclerView.Adapter<DailyForecastAdapter.ViewHolder> {

    private List<WeatherDaily> forecasts = new ArrayList<>();
    private final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE", Locale.CHINESE);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());

    /**
     * 更新天气预报数据
     * 
     * @param newForecasts 新的天气预报数据列表
     */
    public void updateForecasts(List<WeatherDaily> newForecasts) {
        this.forecasts.clear();
        this.forecasts.addAll(newForecasts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_forecast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherDaily forecast = forecasts.get(position);

        // 设置日期
        try {
            Date date = inputFormat.parse(forecast.getFxDate());
            String dayOfWeek = position == 0 ? "今天" : outputFormat.format(date);
            holder.dateText.setText(dayOfWeek);
            holder.dateDetailText.setText(dateFormat.format(date));
        } catch (Exception e) {
            holder.dateText.setText(forecast.getFxDate());
            holder.dateDetailText.setText("");
        }

        // 设置天气图标（使用emoji）
        holder.weatherIcon.setText(WeatherIconUtils.getWeatherEmoji(forecast.getIconDay()));
        holder.weatherIcon.setTextColor(WeatherIconUtils.getWeatherColor(forecast.getIconDay()));

        // 设置天气描述
        holder.weatherText.setText(forecast.getTextDay());

        // 设置温度范围
        holder.tempMinText.setText(forecast.getTempMin() + "°");
        holder.tempMaxText.setText(forecast.getTempMax() + "°");

        // 设置温度进度条背景
        try {
            int minTemp = Integer.parseInt(forecast.getTempMin());
            int maxTemp = Integer.parseInt(forecast.getTempMax());
            int color = WeatherIconUtils.getWeatherColor(forecast.getIconDay());

            // 创建渐变背景
            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    new int[] { 0x33FFFFFF, color & 0x33FFFFFF });
            gradient.setCornerRadius(holder.tempProgressBar.getHeight() / 2f);
            holder.tempProgressBar.setBackground(gradient);
        } catch (Exception e) {
            // 如果解析温度失败，使用默认背景
            holder.tempProgressBar.setBackgroundColor(0x33FFFFFF);
        }
    }

    @Override
    public int getItemCount() {
        return forecasts.size();
    }

    /**
     * ViewHolder类
     * 用于缓存列表项中的视图
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        TextView dateDetailText;
        TextView weatherIcon;
        TextView weatherText;
        TextView tempMinText;
        TextView tempMaxText;
        View tempProgressBar;

        ViewHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            dateDetailText = itemView.findViewById(R.id.dateDetailText);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
            weatherText = itemView.findViewById(R.id.weatherText);
            tempMinText = itemView.findViewById(R.id.tempMinText);
            tempMaxText = itemView.findViewById(R.id.tempMaxText);
            tempProgressBar = itemView.findViewById(R.id.tempProgressBar);
        }
    }
}