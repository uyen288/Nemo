package com.example.nemo.util;

import androidx.appcompat.app.AppCompatDelegate;

public class MyApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        boolean isDark = SharePrefManager.getInstance(this).isDarkMode();

        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}