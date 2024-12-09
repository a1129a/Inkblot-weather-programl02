package com.example.myapplication24.service;

import android.location.Location;

/**
 * 定位服务接口
 * 定义统一的定位服务接口，支持不同的定位实现
 */
public interface ILocationService {
    interface OnLocationResultListener {
        void onLocationResult(Location location);

        void onLocationError(String error);
    }

    void requestLocationUpdates(OnLocationResultListener listener);

    void stopLocationUpdates();

    void handlePermissionResult(int requestCode, String[] permissions, int[] grantResults);
}