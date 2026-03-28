package com.wildfire.nemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wildfire.nemo.R;
import com.wildfire.nemo.data.model.Topic;
import com.wildfire.nemo.util.SharePrefManager;
import com.wildfire.nemo.view.activity.VocabularyActivity;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {
    private List<Topic> topicList;
    private Context context;

    public TopicAdapter(Context context, List<Topic> topicList) {
        this.context = context;
        this.topicList = topicList;
    }

    public void updateList(List<Topic> newList) {
        this.topicList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Topic topic = topicList.get(position);

        String currentLang = SharePrefManager.getInstance(context).getLanguage();
        holder.tvTopicName.setText(currentLang.equals("vi") ? topic.getNameVi() : topic.getNameEn());

        String imagePath = "file:///android_asset/images/" + topic.getImage();

        Glide.with(context)
                .load(imagePath)
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.img_loading)
                .centerCrop()
                .into(holder.ivTopicImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VocabularyActivity.class);
            intent.putExtra("TOPIC_ID", topic.getId());
            intent.putExtra("TOPIC_NAME", currentLang.equals("vi") ? topic.getNameVi() : topic.getNameEn());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return topicList != null ? topicList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTopicImage;
        TextView tvTopicName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTopicImage = itemView.findViewById(R.id.iv_topic_image);
            tvTopicName = itemView.findViewById(R.id.tv_topic_name);
        }
    }
}
