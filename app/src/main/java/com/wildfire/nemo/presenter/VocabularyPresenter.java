package com.wildfire.nemo.presenter;

import android.util.Log;
import com.wildfire.nemo.contract.IVocabularyContract;
import com.wildfire.nemo.data.model.Vocabulary;
import com.wildfire.nemo.data.remote.ApiService;
import com.wildfire.nemo.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VocabularyPresenter implements IVocabularyContract.IPresenter {
    private static final String TAG = "VocabularyPresenter";
    private IVocabularyContract.IView mView;
    private ApiService apiService;

    public VocabularyPresenter(IVocabularyContract.IView view) {
        this.mView = view;
        this.apiService = RetrofitClient.getApiService();
    }

    @Override
    public void loadVocabularyByTopic(int topicId) {
        if (mView != null) mView.showProgress();
        apiService.getVocabularyByTopic(topicId).enqueue(new Callback<List<Vocabulary>>() {
            @Override
            public void onResponse(Call<List<Vocabulary>> call, Response<List<Vocabulary>> response) {
                if (mView != null) {
                    mView.hideProgress();
                    if (response.isSuccessful() && response.body() != null) {
                        List<Vocabulary> filteredList = new ArrayList<>();
                        for (Vocabulary v : response.body()) {
                            if (v.getTopicId() == topicId) {
                                filteredList.add(v);
                            }
                        }
                        Log.d(TAG, "Filtered Vocab for Topic " + topicId + ": " + filteredList.size());
                        mView.updateVocabularyUI(filteredList);
                    } else {
                        mView.onError("Failed to load vocabulary");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Vocabulary>> call, Throwable t) {
                if (mView != null) {
                    mView.hideProgress();
                    mView.onError(t.getMessage());
                }
            }
        });
    }

    @Override
    public void loadVocabularyByLevel(String level) {
        if (mView != null) mView.showProgress();
        apiService.getVocabularyByLevel(level).enqueue(new Callback<List<Vocabulary>>() {
            @Override
            public void onResponse(Call<List<Vocabulary>> call, Response<List<Vocabulary>> response) {
                if (mView != null) {
                    mView.hideProgress();
                    if (response.isSuccessful() && response.body() != null) {
                        List<Vocabulary> filteredList = new ArrayList<>();
                        for (Vocabulary v : response.body()) {
                            if (level.equalsIgnoreCase(v.getLevel())) {
                                filteredList.add(v);
                            }
                        }
                        Log.d(TAG, "Filtered Vocab for Level " + level + ": " + filteredList.size());
                        mView.updateVocabularyUI(filteredList);
                    } else {
                        mView.onError("Failed to load vocabulary");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Vocabulary>> call, Throwable t) {
                if (mView != null) {
                    mView.hideProgress();
                    mView.onError(t.getMessage());
                }
            }
        });
    }

    @Override
    public void onSpeakerIconClick(String text) {
        if (mView != null && text != null && !text.isEmpty()) {
            mView.playAudio(text);
        }
    }
}
