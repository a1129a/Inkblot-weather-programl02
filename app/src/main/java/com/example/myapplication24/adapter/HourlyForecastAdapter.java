package com.example.myapplication24.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication24.R;
import com.example.myapplication24.model.WeatherHourly;
import com.example.myapplication24.utils.WeatherIconUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 小时天气预报适配器
 * 用于显示24小时天气预报列表
 */
public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder> {

    private List<WeatherHourly> forecasts = new ArrayList<>();
    private final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
    private final SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    /**
     * 更新天气预报数据
     * 
     * @param newForecasts 新的天气预报数据列表
     */
    public void updateForecasts(List<WeatherHourly> newForecasts) {
        this.forecasts.clear();
        this.forecasts.addAll(newForecasts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hourly_forecast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherHourly forecast = forecasts.get(position);

        // 设置时间
        try {
            Date date = inputFormat.parse(forecast.getFxTime());
            String timeText = position == 0 ? "现在" : outputFormat.format(date);
            holder.timeText.setText(timeText);
        } catch (Exception e) {
            holder.timeText.setText(forecast.getFxTime());
        }

        // 设置天气图标（使用emoji）
        holder.weatherIcon.setText(WeatherIconUtils.getWeatherEmoji(forecast.getIcon()));
        holder.weatherIcon.setTextColor(WeatherIconUtils.getWeatherColor(forecast.getIcon()));

        // 设置温度
        holder.tempText.setText(forecast.getTemp() + "°");

        // 设置降水概率
        holder.precipText.setText(forecast.getPrecip() + "%");
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
        TextView timeText;
        TextView weatherIcon;
        TextView tempText;
        TextView precipText;

        ViewHolder(View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.timeText);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
            tempText = itemView.findViewById(R.id.tempText);
            precipText = itemView.findViewById(R.id.precipText);
        }
    }
}