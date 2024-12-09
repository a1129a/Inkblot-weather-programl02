package com.example.myapplication24.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myapplication24.model.City;
import com.example.myapplication24.repository.CityRepository;

import java.util.List;

public class CityViewModel extends AndroidViewModel {
    private final CityRepository repository;

    public CityViewModel(@NonNull Application application) {
        super(application);
        repository = new CityRepository(application);
    }

    public LiveData<List<City>> getAllCities() {
        return repository.getAllCities();
    }

    public LiveData<List<City>> getFavoriteCities() {
        return repository.getFavoriteCities();
    }

    public LiveData<City> getSelectedCity() {
        return repository.getSelectedCity();
    }

    public LiveData<List<City>> getRecentCities() {
        return repository.getRecentCities();
    }

    public LiveData<List<City>> searchCities(String keyword) {
        return repository.searchCities(keyword);
    }

    public void setSelectedCity(City city) {
        repository.setSelectedCity(city);
    }

    public void toggleFavorite(City city) {
        repository.toggleFavorite(city);
    }

    public void updateAccessTime(City city) {
        repository.updateAccessTime(city);
    }

    public void insert(City city) {
        repository.insert(city);
    }

    public void delete(City city) {
        repository.delete(city);
    }

    public void update(City city) {
        repository.update(city);
    }

    public void clearSelectedCity(Runnable onComplete) {
        repository.clearSelectedCity(onComplete);
    }
}