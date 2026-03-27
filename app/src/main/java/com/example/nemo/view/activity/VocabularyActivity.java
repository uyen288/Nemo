package com.example.nemo.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import com.example.nemo.presenter.VocabularyPresenter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VocabularyActivity extends AppCompatActivity implements IVocabularyConstract.IView {
    private RecyclerView rvVocabulary;
    private ProgressBar progressBar;
    private VocabularyPresenter presenter;
    private FloatingActionButton fabQuiz;
    private Toolbar toolbar;
    private EditText etSearchVocabulary;
    private VocabularyAdapter vocabularyAdapter;
    private List<Vocabulary> fullVocabularyList = new ArrayList<>();
    private TextToSpeech tts;
    private boolean isTtsInitialized = false;
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initViews();
        setupToolbar();
        setupTTS();

        presenter = new VocabularyPresenter(this);

        int topicId = getIntent().getIntExtra("TOPIC_ID", -1);
        String level = getIntent().getStringExtra("LEVEL");
        String topicName = getIntent().getStringExtra("TOPIC_NAME");

        if (topicName != null) {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle(topicName);
        } else if (level != null) {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Level: " + level);
        }

        if (topicId != -1) {
            presenter.loadVocabularyByTopic(topicId);
        } else if (level != null) {
            presenter.loadVocabularyByLevel(level);
        }

        fabQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(VocabularyActivity.this, QuizActivity.class);
            intent.putExtra("TOPIC_ID", topicId);
            intent.putExtra("LEVEL", level);
            intent.putExtra("TOPIC_NAME", topicName);
            startActivity(intent);
        });

        setupSearch();
    }

    private void initViews() {
        rvVocabulary = findViewById(R.id.rv_vocabulary);
        progressBar = findViewById(R.id.progress_bar);
        fabQuiz = findViewById(R.id.fab_quiz);
        toolbar = findViewById(R.id.toolbar);
        etSearchVocabulary = findViewById(R.id.et_search_vocabulary);

        rvVocabulary.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupTTS() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Locale spanish = new Locale("es", "ES");
                int result = tts.setLanguage(spanish);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Try generic Spanish if ES specific fails
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

    private void setupSearch() {
        if (etSearchVocabulary != null) {
            etSearchVocabulary.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    searchRunnable = () -> filterVocabulary(s.toString());
                    searchHandler.postDelayed(searchRunnable, 300);
                }
            });
        }
    }

    private String normalize(String input) {
        if (input == null) return "";
        String normalized = java.text.Normalizer.normalize(input.toLowerCase(), java.text.Normalizer.Form.NFC);
        return normalized.trim();
    }

    private void filterVocabulary(String query) {
        if (fullVocabularyList == null) return;

        if (query.isEmpty()) {
            vocabularyAdapter.updateList(fullVocabularyList);
            return;
        }

        String cleanQuery = normalize(query);
        List<Vocabulary> filteredList = new ArrayList<>();

        for (Vocabulary vocab : fullVocabularyList) {
            String word = normalize(vocab.getWord());
            String meaningVi = normalize(vocab.getMeaningVi());
            String meaningEn = normalize(vocab.getMeaningEn());
            if (word.contains(cleanQuery) ||
                    meaningVi.contains(cleanQuery) ||
                    meaningEn.contains(cleanQuery)) {
                filteredList.add(vocab);
            }
        }
        if (vocabularyAdapter != null) {
            vocabularyAdapter.updateList(filteredList);
        }
    }

    @Override
    public void updateVocabularyUI(List<Vocabulary> vocabularyList) {
        this.fullVocabularyList = vocabularyList;
        if (vocabularyAdapter == null) {
            vocabularyAdapter = new VocabularyAdapter(this, new ArrayList<>(vocabularyList), presenter);
            rvVocabulary.setAdapter(vocabularyAdapter);
        } else {
            vocabularyAdapter.updateList(new ArrayList<>(vocabularyList));
        }
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
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
