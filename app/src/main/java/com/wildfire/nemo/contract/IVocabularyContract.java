package com.wildfire.nemo.contract;

import com.wildfire.nemo.data.model.Vocabulary;
import java.util.List;

public interface IVocabularyContract {
    interface IView {
        void updateVocabularyUI(List<Vocabulary> vocabularyList);
        void showProgress();
        void hideProgress();
        void onError(String message);
        void playAudio(String text);
    }

    interface IPresenter {
        void loadVocabularyByTopic(int topicId);
        void loadVocabularyByLevel(String level);
        void onSpeakerIconClick(String text);
    }
}
