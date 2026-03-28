package com.wildfire.nemo.presenter;

import android.util.Log;
import com.wildfire.nemo.contract.IHomeContract;
import com.wildfire.nemo.data.model.Topic;
import com.wildfire.nemo.data.remote.ApiService;
import com.wildfire.nemo.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePresenter implements IHomeContract.IPresenter {
    private static final String TAG = "HomePresenter";
    private IHomeContract.IView mView;
    private ApiService apiService;

    public HomePresenter(IHomeContract.IView view) {
        this.mView = view;
        this.apiService = RetrofitClient.getApiService();
    }

    @Override
    public void loadTopics() {
        if (mView != null) mView.showProgress();
        
        apiService.getTopics().enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(Call<List<Topic>> call, Response<List<Topic>> response) {
                if (mView != null) {
                    mView.hideProgress();
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        Log.d(TAG, "Topics loaded: " + response.body().size());
                        mView.updateTopicUI(response.body());
                    } else {
                        Log.e(TAG, "Topics response empty or failed");
                        loadDummyTopics();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Topic>> call, Throwable t) {
                Log.e(TAG, "Topics API Failure: " + t.getMessage());
                if (mView != null) {
                    mView.hideProgress();
                    loadDummyTopics();
                }
            }
        });
    }

    @Override
    public void loadLevels() {
        if (mView != null) mView.showProgress();
        
        apiService.getLevels().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (mView != null) {
                    mView.hideProgress();
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        Log.d(TAG, "Levels loaded: " + response.body().size());
                        mView.updateLevelUI(response.body());
                    } else {
                        Log.e(TAG, "Levels response empty or failed");
                        loadDummyLevels();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e(TAG, "Levels API Failure: " + t.getMessage());
                if (mView != null) {
                    mView.hideProgress();
                    loadDummyLevels();
                }
            }
        });
    }

    private void loadDummyTopics() {
        List<Topic> dummy = new ArrayList<>();
        dummy.add(new Topic(1, "Numbers", "Du lịch", "", "", "https://via.placeholder.com/150"));
        dummy.add(new Topic(2, "Food", "Ẩm thực", "", "", "https://via.placeholder.com/150"));
        dummy.add(new Topic(3, "Colours", "Màu sắc", "", "", "https://via.placeholder.com/150"));
        dummy.add(new Topic(4, "Family", "Gia đình", "", "", "https://via.placeholder.com/150"));
        mView.updateTopicUI(dummy);
    }

    private void loadDummyLevels() {
        mView.updateLevelUI(Arrays.asList("A1", "A2", "B1", "B2", "C1", "C2"));
    }
}
