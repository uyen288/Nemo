package com.wildfire.nemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wildfire.nemo.R;
import com.wildfire.nemo.data.model.Grammar;
import com.wildfire.nemo.util.SharePrefManager;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class GrammarAdapter extends RecyclerView.Adapter<GrammarAdapter.ViewHolder> {
    private List<Grammar> grammarList;
    private Context context;
    private OnItemClickListener listener;
    private String langCode;

    public interface OnItemClickListener {
        void onItemClick(Grammar grammar);
    }

    public GrammarAdapter(Context context, List<Grammar> grammarList, OnItemClickListener listener) {
        this.context = context;
        this.grammarList = grammarList;
        this.listener = listener;
        this.langCode = SharePrefManager.getInstance(context).getLanguage();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grammar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Grammar grammar = grammarList.get(position);
        holder.tvTitle.setText(grammar.getLocalizedTitle(langCode));
        holder.tvDescription.setText(grammar.getLocalizedDescription(langCode));

        // Dummy duration and popular tag logic
        holder.tvDuration.setText("15 mins");
        holder.tvPopularTag.setVisibility(position == 0 ? View.VISIBLE : View.GONE);

        holder.btnStart.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(grammar);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(grammar);
        });
    }

    @Override
    public int getItemCount() {
        return grammarList != null ? grammarList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDuration, tvPopularTag;
        MaterialButton btnStart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_grammar_title);
            tvDescription = itemView.findViewById(R.id.tv_grammar_description);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvPopularTag = itemView.findViewById(R.id.tv_popular_tag);
            btnStart = itemView.findViewById(R.id.btn_start_lesson);
        }
    }
}
