package com.example.nemo.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemo.R;
import com.example.nemo.adapter.VocabularyAdapter;
import com.example.nemo.constract.IVocabularyConstract;
import com.example.nemo.data.model.Vocabulary;
import com.example.nemo.database.DatabaseHelper;
import com.example.nemo.presenter.VocabularyPresenter;
import com.example.nemo.util.SharePrefManager;

import java.util.List;
import java.util.Locale;

public class CollectionActivity extends AppCompatActivity implements IVocabularyConstract.IView {

    private RecyclerView rvCollection;
    private DatabaseHelper dbHelper;
    private VocabularyAdapter adapter;
    private VocabularyPresenter presenter;
    private TextToSpeech tts;
    private boolean isTtsInitialized = false;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        if (!SharePrefManager.getInstance(this).isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem bộ sưu tập", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Collection");
        }

        rvCollection = findViewById(R.id.rv_collection);
        rvCollection.setLayoutManager(new LinearLayoutManager(this));

        currentUserId = SharePrefManager.getInstance(this).getUserId();
        dbHelper = new DatabaseHelper(this);
        presenter = new VocabularyPresenter(this);
        
        setupTTS();
        loadFavorites();
    }

    private void setupTTS() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Locale spanish = new Locale("es", "ES");
                int result = tts.setLanguage(spanish);
                
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    result = tts.setLanguage(new Locale("es"));
                }

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Spanish language not supported");
                } else {
                    isTtsInitialized = true;
                }
            } else {
                Log.e("TTS", "Initialization failed");
            }
        });
    }

    private void loadFavorites() {
        List<Vocabulary> favoriteList = dbHelper.getAllFavorites(currentUserId);
        adapter = new VocabularyAdapter(this, favoriteList, presenter);
        rvCollection.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SharePrefManager.getInstance(this).isLoggedIn()) {
            currentUserId = SharePrefManager.getInstance(this).getUserId();
            loadFavorites();
        }
    }

    @Override
    public void updateVocabularyUI(List<Vocabulary> vocabularyList) {
    }

    @Override
    public void showProgress() {
    }

    @Override
    public void hideProgress() {
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void playAudio(String text) {
        if (isTtsInitialized && tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "vocab_audio");
        } else if (tts == null) {
            setupTTS();
            Toast.makeText(this, "Initializing TTS, please try again", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "TTS not ready or Spanish not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
