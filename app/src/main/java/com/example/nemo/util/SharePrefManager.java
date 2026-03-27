package com.example.nemo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.example.nemo.data.model.User;

import java.util.Locale;

public class SharePrefManager {
    private static final String PREF_NAME = "AppPreferences";

    // key
    private static final String KEY_IS_DARK_MODE = "is_dark_mode";
    private static final String KEY_LANGUAGE = "app_language";

    private static final String KEY_REMINDER_ON = "reminder_on";
    private static final String KEY_REMINDER_HOUR = "reminder_hour";
    private static final String KEY_REMINDER_MINUTE = "reminder_minute";

    private static final String KEY_USER_TOKEN = "user_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private static SharePrefManager instance;
    private SharedPreferences sharedPreferences;

    // constructor
    private SharePrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharePrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharePrefManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveUserLogin(User user) {
        sharedPreferences.edit()
                .putInt(KEY_USER_ID, user.getId())
                .putString(KEY_USERNAME, user.getUsername())
                .putString(KEY_USER_EMAIL, user.getEmail())
                .putString(KEY_USER_TOKEN, user.getToken())
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserToken() {
        return sharedPreferences.getString(KEY_USER_TOKEN, "");
    }

    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    public void logout() {
        sharedPreferences.edit()
                .remove(KEY_USER_ID)
                .remove(KEY_USERNAME)
                .remove(KEY_USER_EMAIL)
                .remove(KEY_USER_TOKEN)
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .apply();
    }

    // clear

    public void clearAll() {
        sharedPreferences.edit().clear().apply();
    }

    public void resetSettingsOnly() {
        sharedPreferences.edit()
                .remove(KEY_IS_DARK_MODE)
                .remove(KEY_LANGUAGE)
                .remove(KEY_REMINDER_ON)
                .remove(KEY_REMINDER_HOUR)
                .remove(KEY_REMINDER_MINUTE)
                .apply();
    }

    // dark mode
    public void setDarkMode(boolean isDark) {
        sharedPreferences.edit().putBoolean(KEY_IS_DARK_MODE, isDark).apply();
    }

    public boolean isDarkMode() {
        // default: light mode
        return sharedPreferences.getBoolean(KEY_IS_DARK_MODE, false);
    }

    // language
    public void setLanguage(String langCode) {
        // langCode: en / vi
        sharedPreferences.edit().putString(KEY_LANGUAGE, langCode).apply();
    }

    public String getLanguage() {
        // default: en
        return sharedPreferences.getString(KEY_LANGUAGE, "en");
    }

    public static class LocaleHelper {

        public static Context wrapContext(Context context, String language) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale)   ;

            Resources res = context.getResources();
            Configuration config = new Configuration(res.getConfiguration());

            config.setLocale(locale);
            return context.createConfigurationContext(config);
        }
    }
//    NOTI

    public void setReminderOn(boolean isOn) {
        sharedPreferences.edit().putBoolean(KEY_REMINDER_ON, isOn).apply();
    }

    public boolean isReminderOn() {
        return sharedPreferences.getBoolean(KEY_REMINDER_ON, false);
    }

    public void setReminderTime(int hour, int minute) {
        sharedPreferences.edit()
                .putInt(KEY_REMINDER_HOUR, hour)
                .putInt(KEY_REMINDER_MINUTE, minute)
                .apply();
    }

    public int getReminderHour() {
        return sharedPreferences.getInt(KEY_REMINDER_HOUR, 19); // mặc định 7h tối
    }

    public int getReminderMinute() {
        return sharedPreferences.getInt(KEY_REMINDER_MINUTE, 0);
    }
}
