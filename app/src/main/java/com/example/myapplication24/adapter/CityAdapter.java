package com.example.myapplication24.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication24.R;
import com.example.myapplication24.model.City;

import java.util.Objects;

public class CityAdapter extends ListAdapter<City, CityAdapter.CityViewHolder> {
    private final OnCityClickListener listener;

    public interface OnCityClickListener {
        void onCityClick(City city);

        void onFavoriteClick(City city);
    }

    public CityAdapter(OnCityClickListener listener) {
        super(new DiffUtil.ItemCallback<City>() {
            @Override
            public boolean areItemsTheSame(@NonNull City oldItem, @NonNull City newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull City oldItem, @NonNull City newItem) {
                return Objects.equals(oldItem.getName(), newItem.getName()) &&
                        Objects.equals(oldItem.getProvince(), newItem.getProvince()) &&
                        Objects.equals(oldItem.getCity(), newItem.getCity()) &&
                        Objects.equals(oldItem.getDistrict(), newItem.getDistrict()) &&
                        oldItem.isFavorite() == newItem.isFavorite() &&
                        oldItem.isSelected() == newItem.isSelected();
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        City city = getItem(position);
        holder.bind(city, listener);
    }

    static class CityViewHolder extends RecyclerView.ViewHolder {
        private final TextView cityName;
        private final TextView cityAddress;
        private final ImageButton favoriteButton;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.cityName);
            cityAddress = itemView.findViewById(R.id.cityAddress);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
        }

        public void bind(City city, OnCityClickListener listener) {
            cityName.setText(city.getDistrict() != null ? city.getDistrict() : city.getName());

            String address = city.getFullAddress();
            if (address != null && !address.isEmpty()) {
                cityAddress.setVisibility(View.VISIBLE);
                cityAddress.setText(address);
            } else {
                cityAddress.setVisibility(View.GONE);
            }

            favoriteButton.setImageResource(
                    city.isFavorite() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

            itemView.setOnClickListener(v -> listener.onCityClick(city));
            favoriteButton.setOnClickListener(v -> listener.onFavoriteClick(city));
        }
    }
}