package com.wildfire.nemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wildfire.nemo.R;
import com.wildfire.nemo.util.SharePrefManager;
import com.wildfire.nemo.view.activity.VocabularyActivity;

import java.util.List;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.ViewHolder> {
    private List<String> levelList;
    private Context context;

    public LevelAdapter(Context context, List<String> levelList) {
        this.context = context;
        this.levelList = levelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_level, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String level = levelList.get(position).toUpperCase();
        holder.tvLevelIcon.setText(level);

        String lang = SharePrefManager.getInstance(context).getLanguage();
        boolean isVi = "vi".equals(lang);

        String description;
        switch (level) {
            case "A1":
                description = isVi ? "CƠ BẢN" : "BEGINNER";
                break;
            case "A2":
                description = isVi ? "SƠ CẤP" : "ELEMENTARY";
                break;
            case "B1":
            case "B2":
                description = isVi ? "TRUNG CẤP" : "INTERMEDIATE";
                break;
            case "C1":
                description = isVi ? "CAO CẤP" : "ADVANCED";
                break;
            case "C2":
                description = isVi ? "THÀNH THẠO" : "PROFICIENCY";
                break;
            default:
                description = isVi ? "NGƯỜI HỌC" : "LEARNER";
                break;
        }

        holder.tvLevelDescription.setText(description);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VocabularyActivity.class);
            intent.putExtra("LEVEL", level);
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return levelList != null ? levelList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLevelIcon;
        TextView tvLevelDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLevelIcon = itemView.findViewById(R.id.tv_level_icon);
            tvLevelDescription = itemView.findViewById(R.id.tv_level_description);
        }
    }
}
