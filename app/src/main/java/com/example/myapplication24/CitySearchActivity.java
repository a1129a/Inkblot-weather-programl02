package com.example.myapplication24;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication24.adapter.CityAdapter;
import com.example.myapplication24.model.City;
import com.example.myapplication24.viewmodel.CitySearchViewModel;
import com.example.myapplication24.viewmodel.CityViewModel;

public class CitySearchActivity extends AppCompatActivity implements CityAdapter.OnCityClickListener {
    private CityViewModel cityViewModel;
    private CitySearchViewModel searchViewModel;
    private CityAdapter cityAdapter;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_search);

        // 设置工具栏
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 初始化ViewModel
        cityViewModel = new ViewModelProvider(this).get(CityViewModel.class);
        searchViewModel = new ViewModelProvider(this).get(CitySearchViewModel.class);

        // 设置搜索输入框
        EditText searchInput = findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                performSearch(s.toString());
            }
        });
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(v.getText().toString());
                return true;
            }
            return false;
        });

        // 设置RecyclerView
        RecyclerView searchResults = findViewById(R.id.searchResults);
        searchResults.setLayoutManager(new LinearLayoutManager(this));
        cityAdapter = new CityAdapter(this);
        searchResults.setAdapter(cityAdapter);

        // 设置空视图
        emptyView = findViewById(R.id.emptyView);
    }

    private void performSearch(String keyword) {
        if (keyword.isEmpty()) {
            cityAdapter.submitList(null);
            emptyView.setVisibility(View.GONE);
            return;
        }

        searchViewModel.searchCities(keyword).observe(this, cities -> {
            cityAdapter.submitList(cities);
            emptyView.setVisibility(cities.isEmpty() ? View.VISIBLE : View.GONE);
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
        // 更新最后访问时间
        city.setLastAccessTime(System.currentTimeMillis());

        // 先清除其他城市的选中状态，然后设置当前城市为选中状态
        cityViewModel.clearSelectedCity(() -> {
            city.setSelected(true);
            cityViewModel.insert(city);

            // 等待数据库操作完成后再跳转
            cityViewModel.getSelectedCity().observe(this, selectedCity -> {
                if (selectedCity != null && selectedCity.getLocationId().equals(city.getLocationId())) {
                    // 返回到主界面
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });
        });
    }

    @Override
    public void onFavoriteClick(City city) {
        cityViewModel.toggleFavorite(city);
    }
}