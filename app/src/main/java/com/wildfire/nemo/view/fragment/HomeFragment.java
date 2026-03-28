package com.wildfire.nemo.view.fragment;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wildfire.nemo.R;
import com.wildfire.nemo.adapter.LevelAdapter;
import com.wildfire.nemo.adapter.TopicAdapter;
import com.wildfire.nemo.contract.IHomeContract;
import com.wildfire.nemo.data.model.Topic;
import com.wildfire.nemo.presenter.HomePresenter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements IHomeContract.IView {
    private HomePresenter mPresenter;
    private RecyclerView rvTopic, rvLevel;
    private ProgressBar progressBar;
    private EditText etSearchTopic;
    private TextView tvNoResults;
    private TopicAdapter topicAdapter;
    private List<Topic> fullTopicList = new ArrayList<>();

    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvTopic = view.findViewById(R.id.rv_topic);
        rvLevel = view.findViewById(R.id.rv_level);
        progressBar = view.findViewById(R.id.progress_bar);
        etSearchTopic = view.findViewById(R.id.et_search_topic);
        tvNoResults = view.findViewById(R.id.tv_no_results);

        if (rvTopic != null) {
            rvTopic.setLayoutManager(new GridLayoutManager(getContext(), 2));
            rvTopic.setNestedScrollingEnabled(false);
        }

        if (rvLevel != null) {
            rvLevel.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            rvLevel.setNestedScrollingEnabled(true);
        }

        mPresenter = new HomePresenter(this);
        mPresenter.loadTopics();
        mPresenter.loadLevels();

        setupSearch();


        NestedScrollView scrollView = view.findViewById(R.id.main_scrollview);
        com.google.android.material.button.MaterialButton btnStart = view.findViewById(R.id.btn_start_learning);
        TextView tvTopicHeader = view.findViewById(R.id.tv_topic_header);

        if (btnStart != null && scrollView != null && tvTopicHeader != null) {
            btnStart.setOnClickListener(v -> {
                int targetY = tvTopicHeader.getTop();

                ObjectAnimator.ofInt(scrollView, "scrollY", targetY)
                        .setDuration(1000)
                        .start();
            });
        }
    }

    private void setupSearch() {
        if (etSearchTopic != null) {
            etSearchTopic.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (searchRunnable != null) {
                        searchHandler.removeCallbacks(searchRunnable);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    searchRunnable = () -> {
                        filterTopics(s.toString());
                    };
                    searchHandler.postDelayed(searchRunnable, 500);
                }
            });
        }
    }

    private String normalize(String input) {
        if (input == null) return "";
        return java.text.Normalizer.normalize(input.toLowerCase(), java.text.Normalizer.Form.NFC).trim();
    }

    private void filterTopics(String query) {
        if (fullTopicList == null || topicAdapter == null) return;

        if (query.isEmpty()) {
            topicAdapter.updateList(new ArrayList<>(fullTopicList));
            tvNoResults.setVisibility(View.GONE);
            return;
        }

        String cleanQuery = normalize(query);
        List<Topic> filteredList = new ArrayList<>();

        for (Topic topic : fullTopicList) {
            String nameEn = normalize(topic.getNameEn());
            String nameVi = normalize(topic.getNameVi());

            if (nameEn.contains(cleanQuery) || nameVi.contains(cleanQuery)) {
                filteredList.add(topic);
            }
        }

        if (filteredList.isEmpty()) {
            tvNoResults.setVisibility(View.VISIBLE);
        } else {
            tvNoResults.setVisibility(View.GONE);
        }

        topicAdapter.updateList(filteredList);
    }

    @Override
    public void updateTopicUI(List<Topic> topicList) {
        this.fullTopicList = topicList;
        if (rvTopic != null) {
            if (topicAdapter == null) {
                topicAdapter = new TopicAdapter(getContext(), new ArrayList<>(topicList));
                rvTopic.setAdapter(topicAdapter);
            } else {
                topicAdapter.updateList(new ArrayList<>(topicList));
            }
        }
    }

    @Override
    public void updateLevelUI(List<String> levelList) {
        if (rvLevel != null) {
            LevelAdapter adapter = new LevelAdapter(getContext(), levelList);
            rvLevel.setAdapter(adapter);
        }
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
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        super.onDestroyView();
    }
}
