package com.wildfire.nemo.presenter;

import com.wildfire.nemo.contract.IQuizContract;
import com.wildfire.nemo.data.model.Topic;
import com.wildfire.nemo.data.model.Vocabulary;
import com.wildfire.nemo.data.remote.ApiService;
import com.wildfire.nemo.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizPresenter implements IQuizContract.IPresenter {
    private IQuizContract.IView mView;
    private ApiService apiService;

    public QuizPresenter(IQuizContract.IView view) {
        this.mView = view;
        this.apiService = RetrofitClient.getApiService();
    }

    @Override
    public void loadInitialData() {
        if (mView != null) mView.showProgress();

        // Load Topics
        apiService.getTopics().enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(Call<List<Topic>> call, Response<List<Topic>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Topic> topics = new ArrayList<>();
                    // Add "All" topic
                    Topic allTopic = new Topic();
                    allTopic.setId(-1);
                    allTopic.setNameEn("All Topics");
                    allTopic.setNameVi("Tất cả chủ đề");
                    topics.add(allTopic);
                    topics.addAll(response.body());
                    if (mView != null) mView.showTopics(topics);
                }
                loadLevels();
            }

            @Override
            public void onFailure(Call<List<Topic>> call, Throwable t) {
                if (mView != null) {
                    mView.hideProgress();
                    mView.onError(t.getMessage());
                }
            }
        });
    }

    private void loadLevels() {
        if (mView != null) mView.hideProgress();
        List<String> levels = new ArrayList<>(Arrays.asList("All Levels", "A1", "A2", "B1", "B2", "C1", "C2"));
        if (mView != null) mView.showLevels(levels);
    }

    @Override
    public void prepareQuiz(Integer topicId, String level) {
        if (mView != null) mView.showProgress();

        // Vẫn gọi API như bình thường
        apiService.getVocabulary(null, null).enqueue(new Callback<List<Vocabulary>>() {
            @Override
            public void onResponse(Call<List<Vocabulary>> call, Response<List<Vocabulary>> response) {
                if (mView != null) mView.hideProgress();

                if (response.isSuccessful() && response.body() != null) {
                    List<Vocabulary> allData = response.body();
                    List<Vocabulary> filteredList = new ArrayList<>();

                    // --- LOGIC LỌC TẠI ĐÂY ---
                    for (Vocabulary v : allData) {
                        boolean matchesTopic = (topicId == -1 || v.getTopicId() == topicId);
                        boolean matchesLevel = (level == null || level.equalsIgnoreCase("All Levels") || v.getLevel().equalsIgnoreCase(level));

                        if (matchesTopic && matchesLevel) {
                            filteredList.add(v);
                        }
                    }

                    if (filteredList.size() < 4) {
                        mView.onError("Chủ đề này chỉ có " + filteredList.size() + " từ, không đủ 4 từ để tạo đáp án.");
                        return;
                    }

                    Collections.shuffle(filteredList);
                    mView.startQuiz(filteredList);
                }
            }

            @Override
            public void onFailure(Call<List<Vocabulary>> call, Throwable t) {
                if (mView != null) {
                    mView.hideProgress();
                    mView.onError("Lỗi kết nối: " + t.getMessage());
                }
            }
        });
    }
}
