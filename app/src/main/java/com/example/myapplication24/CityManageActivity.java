package com.example.myapplication24;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication24.adapter.CityAdapter;
import com.example.myapplication24.model.City;
import com.example.myapplication24.viewmodel.CityViewModel;

public class CityManageActivity extends AppCompatActivity implements CityAdapter.OnCityClickListener {
    private CityViewModel cityViewModel;
    private CityAdapter cityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_manage);

        // 设置工具栏
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 初始化ViewModel
        cityViewModel = new ViewModelProvider(this).get(CityViewModel.class);

        // 设置搜索按钮
        TextView searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CitySearchActivity.class);
            startActivity(intent);
        });

        // 设置RecyclerView
        RecyclerView cityList = findViewById(R.id.cityList);
        cityList.setLayoutManager(new LinearLayoutManager(this));
        cityAdapter = new CityAdapter(this);
        cityList.setAdapter(cityAdapter);

        // 观察城市列表数据变化
        cityViewModel.getAllCities().observe(this, cities -> {
            cityAdapter.submitList(cities);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCityClick(City city) {
        cityViewModel.setSelectedCity(city);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onFavoriteClick(City city) {
        cityViewModel.toggleFavorite(city);
    }
}