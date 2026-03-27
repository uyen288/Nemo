package com.example.nemo.presenter;

import com.example.nemo.constract.IGrammarConstract;
import com.example.nemo.data.model.Grammar;
import com.example.nemo.data.remote.ApiService;
import com.example.nemo.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GrammarPresenter implements IGrammarConstract.IPresenter {
    private IGrammarConstract.IView mView;
    private ApiService apiService;
    private List<Grammar> fullList = new ArrayList<>();

    public GrammarPresenter(IGrammarConstract.IView view) {
        this.mView = view;
        this.apiService = RetrofitClient.getApiService();
    }

    @Override
    public void loadGrammarLessons() {
        if (mView != null) mView.showProgress();
        apiService.getGrammarLessons().enqueue(new Callback<List<Grammar>>() {
            @Override
            public void onResponse(Call<List<Grammar>> call, Response<List<Grammar>> response) {
                if (mView != null) {
                    mView.hideProgress();
                    if (response.isSuccessful() && response.body() != null) {
                        fullList = response.body();
                        mView.updateGrammarUI(fullList);
                    } else {
                        mView.onError("Failed to load grammar lessons");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Grammar>> call, Throwable t) {
                if (mView != null) {
                    mView.hideProgress();
                    mView.onError(t.getMessage());
                }
            }
        });
    }

    @Override
    public void searchGrammar(String query) {
        if (query == null || query.isEmpty()) {
            if (mView != null) mView.updateGrammarUI(fullList);
            return;
        }
        String lowerQuery = query.toLowerCase();
        List<Grammar> filteredList = new ArrayList<>();
        for (Grammar g : fullList) {
            boolean matches = (g.getTitleEn() != null && g.getTitleEn().toLowerCase().contains(lowerQuery)) ||
                             (g.getTitleVi() != null && g.getTitleVi().toLowerCase().contains(lowerQuery)) ||
                             (g.getDescriptionEn() != null && g.getDescriptionEn().toLowerCase().contains(lowerQuery)) ||
                             (g.getDescriptionVi() != null && g.getDescriptionVi().toLowerCase().contains(lowerQuery));
            if (matches) {
                filteredList.add(g);
            }
        }
        if (mView != null) mView.updateGrammarUI(filteredList);
    }
}
