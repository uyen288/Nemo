package com.wildfire.nemo.view.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.wildfire.nemo.util.SharePrefManager;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        boolean isDark = SharePrefManager.getInstance(this).isDarkMode();

        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = SharePrefManager.getInstance(newBase).getLanguage();

        Context context = SharePrefManager.LocaleHelper.wrapContext(newBase, lang);
        super.attachBaseContext(context);
    }
}