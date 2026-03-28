package com.wildfire.nemo.contract;

import com.wildfire.nemo.data.model.Topic;

import java.util.List;

public interface IHomeContract {
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
