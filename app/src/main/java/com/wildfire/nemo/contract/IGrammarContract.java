package com.wildfire.nemo.contract;

import com.wildfire.nemo.data.model.Grammar;
import java.util.List;

public interface IGrammarContract {
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
