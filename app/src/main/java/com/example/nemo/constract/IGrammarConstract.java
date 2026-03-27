package com.example.nemo.constract;

import com.example.nemo.data.model.Grammar;
import java.util.List;

public interface IGrammarConstract {
    interface IView {
        void updateGrammarUI(List<Grammar> grammarList);
        void showProgress();
        void hideProgress();
        void onError(String message);
    }

    interface IPresenter {
        void loadGrammarLessons();
        void searchGrammar(String query);
    }
}
