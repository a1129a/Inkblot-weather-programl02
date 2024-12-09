package com.example.myapplication24.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Android原生定位服务实现
 */
public class LocationService implements ILocationService {
    private static final String TAG = "LocationService";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final long MIN_UPDATE_INTERVAL = 10000; // 最小更新间隔，10秒
    private static final long LOCATION_CACHE_DURATION = 10 * 60 * 1000; // 10分钟

    private final Context context;
    private final LocationManager locationManager;
    private LocationListener locationListener;
    private OnLocationResultListener listener;

    private Location cachedLocation;
    private long lastLocationTime;
    private boolean isRequestingLocation;
    private long lastUpdateTime = 0;

    public LocationService(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void requestLocationUpdates(OnLocationResultListener listener) {
        if (isRequestingLocation) {
            Log.d(TAG, "Location request already in progress");
            return;
        }

        // 检查是否满足最小更新间隔
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - lastUpdateTime < MIN_UPDATE_INTERVAL) {
            if (cachedLocation != null) {
                Log.d(TAG, "Using cached location");
                listener.onLocationResult(cachedLocation);
            }
            return;
        }

        this.listener = listener;
        lastUpdateTime = currentTime;

        // 检查缓存的位置是否仍然有效
        if (cachedLocation != null && currentTime - lastLocationTime < LOCATION_CACHE_DURATION) {
            Log.d(TAG, "Using valid cached location");
            listener.onLocationResult(cachedLocation);
            return;
        }

        if (!checkLocationPermission()) {
            Log.d(TAG, "Requesting location permission");
            if (context instanceof Activity) {
                requestLocationPermission((Activity) context);
            } else {
                listener.onLocationError("缺少定位权限");
            }
            return;
        }

        if (!isLocationEnabled()) {
            Log.d(TAG, "Location services are disabled");
            listener.onLocationError("请开启位置服务");
            return;
        }

        try {
            isRequestingLocation = true;
            requestLocationWithAndroidAPI();
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception while requesting location", e);
            isRequestingLocation = false;
            listener.onLocationError("获取位置信息失败：" + e.getMessage());
        }
    }

    private boolean isLocationEnabled() {
        return locationManager != null &&
                (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    private void requestLocationWithAndroidAPI() {
        try {
            Log.d(TAG, "Requesting location updates with Android API");
            Location lastKnownLocation = null;

            // 尝试获取最后已知位置
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (lastKnownLocation == null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (lastKnownLocation != null &&
                    SystemClock.elapsedRealtime() - lastKnownLocation.getTime() < LOCATION_CACHE_DURATION) {
                Log.d(TAG, "Using last known location");
                updateLocationCache(lastKnownLocation);
                isRequestingLocation = false;
                listener.onLocationResult(lastKnownLocation);
                return;
            }

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    long currentTime = SystemClock.elapsedRealtime();
                    if (currentTime - lastUpdateTime < MIN_UPDATE_INTERVAL) {
                        return;
                    }
                    lastUpdateTime = currentTime;

                    Log.d(TAG, "Location updated: " + location.getLatitude() + ", " + location.getLongitude());
                    updateLocationCache(location);
                    isRequestingLocation = false;
                    listener.onLocationResult(location);
                    stopLocationUpdates();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(@NonNull String provider) {
                    Log.d(TAG, "Location provider enabled: " + provider);
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                    Log.d(TAG, "Location provider disabled: " + provider);
                    isRequestingLocation = false;
                    listener.onLocationError("位置服务已禁用");
                }
            };

            // 优先使用网络定位
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Log.d(TAG, "Requesting network location updates");
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_UPDATE_INTERVAL, 0, locationListener);
            }

            // 同时使用GPS定位以提高精确度
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.d(TAG, "Requesting GPS location updates");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_UPDATE_INTERVAL, 0, locationListener);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception in requestLocationWithAndroidAPI", e);
            isRequestingLocation = false;
            listener.onLocationError("没有位置权限");
        } catch (Exception e) {
            Log.e(TAG, "Error in requestLocationWithAndroidAPI", e);
            isRequestingLocation = false;
            listener.onLocationError("位置服务异常：" + e.getMessage());
        }
    }

    private void updateLocationCache(Location location) {
        Log.d(TAG, "Updating location cache");
        cachedLocation = location;
        lastLocationTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void stopLocationUpdates() {
        Log.d(TAG, "Stopping location updates");
        isRequestingLocation = false;
        if (locationListener != null) {
            locationManager.removeUpdates(locationListener);
            locationListener = null;
        }
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void handlePermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Location permission granted");
                requestLocationUpdates(listener);
            } else {
                Log.d(TAG, "Location permission denied");
                isRequestingLocation = false;
                if (listener != null) {
                    listener.onLocationError("用户拒绝了定位权限");
                }
            }
        }
    }
}