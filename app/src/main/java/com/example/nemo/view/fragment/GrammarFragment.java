package com.example.nemo.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemo.R;
import com.example.nemo.adapter.GrammarAdapter;
import com.example.nemo.constract.IGrammarConstract;
import com.example.nemo.data.model.Grammar;
import com.example.nemo.presenter.GrammarPresenter;
import com.example.nemo.util.SharePrefManager;
import com.example.nemo.view.activity.GrammarDetailActivity;

import java.util.List;

public class GrammarFragment extends Fragment implements IGrammarConstract.IView {
    private GrammarPresenter presenter;
    private RecyclerView rvGrammar;
    private EditText etSearch;
    private ProgressBar progressBar;
    private GrammarAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grammar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvGrammar = view.findViewById(R.id.rv_grammar);
        etSearch = view.findViewById(R.id.et_search_grammar);
        progressBar = view.findViewById(R.id.progress_bar);

        rvGrammar.setLayoutManager(new LinearLayoutManager(getContext()));

        presenter = new GrammarPresenter(this);
        presenter.loadGrammarLessons();

        setupSearch();
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.searchGrammar(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void updateGrammarUI(List<Grammar> grammarList) {
        String langCode = SharePrefManager.getInstance(getContext()).getLanguage();
        adapter = new GrammarAdapter(getContext(), grammarList, grammar -> {
            Intent intent = new Intent(getContext(), GrammarDetailActivity.class);
            intent.putExtra("TITLE", grammar.getLocalizedTitle(langCode));
            intent.putExtra("DESCRIPTION", grammar.getLocalizedDescription(langCode));
            intent.putExtra("CONTENT", grammar.getLocalizedContent(langCode));
            startActivity(intent);
        });
        rvGrammar.setAdapter(adapter);
    }

    @Override
    public void showProgress() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
        }
    }
}
