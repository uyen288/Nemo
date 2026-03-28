package com.wildfire.nemo.view.activity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.wildfire.nemo.R;
import com.wildfire.nemo.util.ApiHelper;

import org.json.JSONObject;

public class VocabularyWidget extends AppWidgetProvider {
    public static final String ACTION_REFRESH = "com.example.spanish.ACTION_REFRESH";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_vocabulary);

        Intent intent = new Intent(context, VocabularyWidget.class);
        intent.setAction(ACTION_REFRESH);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.btn_refresh, pendingIntent);

        views.setTextViewText(R.id.widget_word, "Loading...");
        views.setTextViewText(R.id.widget_phonetic, "Loading...");
        views.setTextViewText(R.id.widget_meaning, "Loading...");
        appWidgetManager.updateAppWidget(appWidgetId, views);

        new Thread(() -> {
            String jsonResponse = ApiHelper.getRandomVocab();
            if (jsonResponse != null) {
                try {
                    org.json.JSONArray arr = new org.json.JSONArray(jsonResponse);

                    int index = new java.util.Random().nextInt(arr.length());
                    JSONObject data = arr.getJSONObject(index);

                    String word = data.optString("word", "");
                    String phonetic = data.optString("phonetic", "");
                    String meaning = data.optString("meaning_en", "");

                    views.setTextViewText(R.id.widget_word, word);
                    views.setTextViewText(R.id.widget_phonetic, phonetic);
                    views.setTextViewText(R.id.widget_meaning, meaning);

                    appWidgetManager.updateAppWidget(appWidgetId, views);

                } catch (Exception e) {
                    e.printStackTrace();
                    views.setTextViewText(R.id.widget_meaning, "Parse Error");
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }
            } else {
                views.setTextViewText(R.id.widget_meaning, "Internet Error");
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }).start();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_REFRESH.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, VocabularyWidget.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            for (int id : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, id);
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}