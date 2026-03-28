package com.wildfire.nemo.contract;

import com.wildfire.nemo.data.model.Topic;
import com.wildfire.nemo.data.model.Vocabulary;

import java.util.List;

public interface IQuizContract {
    interface IView {
        void showTopics(List<Topic> topics);
        void showLevels(List<String> levels);
        void startQuiz(List<Vocabulary> quizVocab);
        void showProgress();
        void hideProgress();
        void onError(String message);
    }

    interface IPresenter {
        void loadInitialData();
        void prepareQuiz(Integer topicId, String level);
    }
}
