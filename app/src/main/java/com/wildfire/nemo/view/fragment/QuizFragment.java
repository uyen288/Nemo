package com.wildfire.nemo.view.fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.wildfire.nemo.R;
import com.wildfire.nemo.contract.IQuizContract;
import com.wildfire.nemo.data.model.Topic;
import com.wildfire.nemo.data.model.Vocabulary;
import com.wildfire.nemo.presenter.QuizPresenter;
import com.wildfire.nemo.util.SharePrefManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizFragment extends Fragment implements IQuizContract.IView {

    private QuizPresenter presenter;
    private LinearLayout layoutSetup, layoutQuiz;
    private Spinner spinnerTopic, spinnerLevel;
    private Button btnStartQuiz;
    private ImageButton btnBackToSetup, btnResetQuiz;
    private ProgressBar loadingBar, quizProgressBar;
    private ImageView ivQuizImage;
    private TextView tvQuizWord, tvQuestion, tvSelectTopicLabel, tvSelectLevelLabel, tvTitleSetup;
    private MaterialButton[] btnOptions = new MaterialButton[4];

    private List<Vocabulary> quizData;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private static final int MAX_QUESTIONS = 10;
    private boolean hasDataFromActivity = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        presenter = new QuizPresenter(this);

        updateStaticText();

        Bundle args = getArguments();
        hasDataFromActivity = args != null && (args.getInt("TOPIC_ID", -1) != -1 || args.getString("LEVEL") != null);

        if (hasDataFromActivity) {
            layoutSetup.setVisibility(View.GONE);
            presenter.prepareQuiz(args.getInt("TOPIC_ID", -1), args.getString("LEVEL"));
        } else {
            layoutSetup.setVisibility(View.VISIBLE);
            layoutQuiz.setVisibility(View.GONE);
            presenter.loadInitialData();
        }

        setupClickListeners();
    }

    private String getCurrentLang() {
        return SharePrefManager.getInstance(requireContext()).getLanguage();
    }

    private boolean isVietnamese() {
        return "vi".equals(getCurrentLang());
    }

    private void updateStaticText() {
        boolean vi = isVietnamese();
        tvTitleSetup.setText(vi ? "Luyện tập Quiz" : "Quiz Practice");
        tvSelectTopicLabel.setText(vi ? "Chọn chủ đề:" : "Select Topic:");
        tvSelectLevelLabel.setText(vi ? "Chọn cấp độ:" : "Select Level:");
        btnStartQuiz.setText(vi ? "Bắt đầu" : "Start Quiz");
//        tvQuestion.setText(vi ? "Đây là từ gì?" : "What is this word?");
    }

    private void setupClickListeners() {
        btnStartQuiz.setOnClickListener(v -> {
            Topic selectedTopic = (Topic) spinnerTopic.getSelectedItem();
            String selectedLevel = (String) spinnerLevel.getSelectedItem();
            if (selectedTopic != null) {
                presenter.prepareQuiz(selectedTopic.getId(), selectedLevel);
            }
        });

        btnBackToSetup.setOnClickListener(v -> {
            boolean vi = isVietnamese();
            new AlertDialog.Builder(requireContext())
                    .setTitle(vi ? "Thoát luyện tập" : "Quit Quiz")
                    .setMessage(vi ? "Bạn có chắc chắn muốn thoát không?" : "Are you sure you want to quit this quiz?")
                    .setPositiveButton(vi ? "Có" : "Yes", (dialog, which) -> {
                        if (hasDataFromActivity) {
                            requireActivity().finish();
                        } else {
                            layoutQuiz.setVisibility(View.GONE);
                            layoutSetup.setVisibility(View.VISIBLE);
                        }
                    })
                    .setNegativeButton(vi ? "Không" : "No", null)
                    .show();
        });

        btnResetQuiz.setOnClickListener(v -> {
            boolean vi = isVietnamese();
            new AlertDialog.Builder(requireContext())
                    .setTitle(vi ? "Làm lại" : "Reset Quiz")
                    .setMessage(vi ? "Bạn có muốn bắt đầu lại từ câu đầu tiên?" : "Do you want to restart from the first question?")
                    .setPositiveButton(vi ? "Có" : "Yes", (dialog, which) -> {
                        if (quizData != null && !quizData.isEmpty()) {
                            startQuiz(quizData);
                        }
                    })
                    .setNegativeButton(vi ? "Không" : "No", null)
                    .show();
        });
    }

    private void initViews(View view) {
        layoutSetup = view.findViewById(R.id.layout_setup);
        layoutQuiz = view.findViewById(R.id.layout_quiz);
        spinnerTopic = view.findViewById(R.id.spinner_topic);
        spinnerLevel = view.findViewById(R.id.spinner_level);
        btnStartQuiz = view.findViewById(R.id.btn_start_quiz);
        btnBackToSetup = view.findViewById(R.id.btn_back_to_setup);
        btnResetQuiz = view.findViewById(R.id.btn_reset_quiz);
        loadingBar = view.findViewById(R.id.loading_bar);
        quizProgressBar = view.findViewById(R.id.quiz_progress);
        ivQuizImage = view.findViewById(R.id.iv_quiz_image);
        tvQuizWord = view.findViewById(R.id.tv_quiz_word);
//        tvQuestion = view.findViewById(R.id.tv_quiz_question);
        
        // Các TextView nhãn
        tvTitleSetup = view.findViewById(R.id.tv_title_setup);
        tvSelectTopicLabel = view.findViewById(R.id.tv_select_topic_label);
        tvSelectLevelLabel = view.findViewById(R.id.tv_select_level_label);

        btnOptions[0] = view.findViewById(R.id.btn_option1);
        btnOptions[1] = view.findViewById(R.id.btn_option2);
        btnOptions[2] = view.findViewById(R.id.btn_option3);
        btnOptions[3] = view.findViewById(R.id.btn_option4);

        for (int i = 0; i < 4; i++) {
            final int index = i;
            btnOptions[i].setOnClickListener(v -> checkAnswer(btnOptions[index].getText().toString()));
        }
    }

    @Override
    public void showTopics(List<Topic> topics) {
        String lang = getCurrentLang();
        ArrayAdapter<Topic> adapter = new ArrayAdapter<Topic>(getContext(), android.R.layout.simple_spinner_item, topics) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                Topic t = getItem(position);
                if (t != null) tv.setText(lang.equals("vi") ? t.getNameVi() : t.getNameEn());
                return tv;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
                Topic t = getItem(position);
                if (t != null) tv.setText(lang.equals("vi") ? t.getNameVi() : t.getNameEn());
                return tv;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTopic.setAdapter(adapter);
    }

    @Override
    public void showLevels(List<String> levels) {
        List<String> translatedLevels = new ArrayList<>();
        for (String level : levels) {
            if (level.equalsIgnoreCase("All Levels")) {
                translatedLevels.add(isVietnamese() ? "Tất cả cấp độ" : "All Levels");
            } else {
                translatedLevels.add(level);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, translatedLevels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(adapter);
    }

    @Override
    public void startQuiz(List<Vocabulary> quizVocab) {
        this.quizData = new ArrayList<>(quizVocab); 
        Collections.shuffle(this.quizData);
        this.currentQuestionIndex = 0;
        this.score = 0;

        layoutSetup.setVisibility(View.GONE);
        layoutQuiz.setVisibility(View.VISIBLE);

        showQuestion();
    }

    private void showQuestion() {
        int totalToQuiz = Math.min(quizData.size(), MAX_QUESTIONS);
        if (currentQuestionIndex >= totalToQuiz) {
            showResult();
            return;
        }

        Vocabulary currentVocab = quizData.get(currentQuestionIndex);
        quizProgressBar.setMax(totalToQuiz);
        quizProgressBar.setProgress(currentQuestionIndex + 1);

        String imagePath = "file:///android_asset/images/" + currentVocab.getImage();
        Glide.with(this)
                .load(imagePath)
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.img_loading)
                .fitCenter()
                .into(ivQuizImage);

        boolean vi = isVietnamese();
        if(vi) tvQuizWord.setText(currentVocab.getMeaningVi());
        else tvQuizWord.setText(currentVocab.getMeaningEn());
        
        List<String> options = new ArrayList<>();
        options.add(currentVocab.getWord());
        
        List<Vocabulary> pool = new ArrayList<>(quizData);
        for (int i = 0; i < pool.size(); i++) {
            if (pool.get(i).getId() == currentVocab.getId()) {
                pool.remove(i);
                break;
            }
        }
        
        Collections.shuffle(pool);
        for (int i = 0; i < 3 && i < pool.size(); i++) {
            options.add(pool.get(i).getWord());
        }
        
        Collections.shuffle(options);
        
        int orangeColor = ContextCompat.getColor(requireContext(), R.color.bg_primary);
        
        for (int i = 0; i < 4; i++) {
            if (i < options.size()) {
                btnOptions[i].setVisibility(View.VISIBLE);
                btnOptions[i].setText(options.get(i));
                
                btnOptions[i].setBackgroundTintList(ColorStateList.valueOf(orangeColor));
                btnOptions[i].setStrokeColor(ColorStateList.valueOf(orangeColor));
                btnOptions[i].setTextColor(Color.WHITE);
                btnOptions[i].setEnabled(true);
            } else {
                btnOptions[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    private void checkAnswer(String selectedAnswer) {
        Vocabulary currentVocab = quizData.get(currentQuestionIndex);
        boolean isCorrect = selectedAnswer.equals(currentVocab.getWord());
        
        int greenColor = ContextCompat.getColor(requireContext(), android.R.color.holo_green_light);
        int redColor = ContextCompat.getColor(requireContext(), android.R.color.holo_red_light);

        for (MaterialButton btn : btnOptions) {
            btn.setEnabled(false);
            String btnText = btn.getText().toString();
            if (btnText.equals(currentVocab.getWord())) {
                btn.setBackgroundTintList(ColorStateList.valueOf(greenColor));
                btn.setStrokeColor(ColorStateList.valueOf(greenColor));
                btn.setTextColor(Color.WHITE);
            } else if (btnText.equals(selectedAnswer) && !isCorrect) {
                btn.setBackgroundTintList(ColorStateList.valueOf(redColor));
                btn.setStrokeColor(ColorStateList.valueOf(redColor));
                btn.setTextColor(Color.WHITE);
            }
        }

        if (isCorrect) score++;

        new Handler().postDelayed(() -> {
            currentQuestionIndex++;
            showQuestion();
        }, 1000);
    }

    private void showResult() {
        int totalQuestions = Math.min(quizData.size(), MAX_QUESTIONS);
        boolean vi = isVietnamese();
        new AlertDialog.Builder(getContext())
                .setTitle(vi ? "Kết thúc luyện tập" : "Quiz Finished")
                .setMessage((vi ? "Điểm của bạn: " : "Your score: ") + score + "/" + totalQuestions)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (hasDataFromActivity) {
                        requireActivity().finish();
                    } else {
                        layoutQuiz.setVisibility(View.GONE);
                        layoutSetup.setVisibility(View.VISIBLE);
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void showProgress() {
        loadingBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        loadingBar.setVisibility(View.GONE);
    }

    @Override
    public void onError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
