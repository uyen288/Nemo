package com.example.nemo.constract;

import com.example.nemo.data.model.Topic;

import java.util.List;

public interface IHomeConstract {
    interface IView {
        void updateLevelUI(List<String> categoryList);
        void updateTopicUI(List<Topic> productList);
        void showProgress();
        void hideProgress();
        void onError(String message);
    }

    interface IPresenter {
        void loadTopics();
        void loadLevels();
    }
}
