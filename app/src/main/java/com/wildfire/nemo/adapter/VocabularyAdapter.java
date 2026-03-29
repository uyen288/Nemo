package com.wildfire.nemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wildfire.nemo.R;
import com.wildfire.nemo.contract.IVocabularyContract;
import com.wildfire.nemo.data.model.Vocabulary;
import com.wildfire.nemo.database.DatabaseHelper;
import com.wildfire.nemo.util.SharePrefManager;
import com.wildfire.nemo.view.activity.LoginActivity;

import java.util.List;

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.ViewHolder> {
    private List<Vocabulary> vocabularyList;
    private Context context;
    private int expandedPosition = -1;
    private DatabaseHelper dbHelper;
    private IVocabularyContract.IPresenter presenter;
    private int currentUserId;

    public VocabularyAdapter(Context context, List<Vocabulary> vocabularyList, IVocabularyContract.IPresenter presenter) {
        this.context = context;
        this.vocabularyList = vocabularyList;
        this.dbHelper = new DatabaseHelper(context);
        this.presenter = presenter;
        this.currentUserId = SharePrefManager.getInstance(context).getUserId();
    }

    public VocabularyAdapter(Context context, List<Vocabulary> vocabularyList) {
        this(context, vocabularyList, null);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage, ivAudio, ivFavorite;
        TextView tvWord, tvPhonetic, tvMeaning, tvExampleEs, tvExampleTranslated;
        LinearLayout layoutExpand;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_vocab_image);
            tvWord = itemView.findViewById(R.id.tv_vocab_word);
            tvPhonetic = itemView.findViewById(R.id.tv_vocab_phonetic);
            tvMeaning = itemView.findViewById(R.id.tv_vocab_meaning);
            ivAudio = itemView.findViewById(R.id.iv_audio);
            ivFavorite = itemView.findViewById(R.id.iv_favorite);
            layoutExpand = itemView.findViewById(R.id.layout_expand);
            tvExampleEs = itemView.findViewById(R.id.tv_example_es);
            tvExampleTranslated = itemView.findViewById(R.id.tv_example_translated);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vocabulary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Vocabulary vocab = vocabularyList.get(position);
        String lang = SharePrefManager.getInstance(context).getLanguage();

        holder.tvWord.setText(vocab.getWord());
        holder.tvPhonetic.setText(vocab.getPhonetic());
        holder.tvMeaning.setText(lang.equals("vi") ? vocab.getMeaningVi() : vocab.getMeaningEn());

        String imagePath = "file:///android_asset/images/" + vocab.getImage();

        Glide.with(context)
                .load(imagePath)
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.img_loading)
                .centerCrop()
                .into(holder.ivImage);

        // Handle Expand/Collapse
        final boolean isExpanded = position == expandedPosition;
        holder.layoutExpand.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);

        if (isExpanded) {
            holder.tvExampleEs.setText(vocab.getExampleEs());
            holder.tvExampleTranslated.setText(lang.equals("vi") ? vocab.getExampleVi() : vocab.getExampleEn());
        }

        holder.itemView.setOnClickListener(v -> {
            int previousExpandedPosition = expandedPosition;
            expandedPosition = isExpanded ? -1 : holder.getAdapterPosition();
            notifyItemChanged(previousExpandedPosition);
            notifyItemChanged(expandedPosition);
        });

        // TTS Audio
        holder.ivAudio.setOnClickListener(v -> {
            if (presenter != null) {
                presenter.onSpeakerIconClick(vocab.getWord());
            }
        });

        // Sync favorite state with DB for the current user
        boolean isLoggedIn = SharePrefManager.getInstance(context).isLoggedIn();
        if (isLoggedIn) {
            boolean isFavorite = dbHelper.isFavorite(vocab.getId(), currentUserId);
            vocab.setFavorite(isFavorite);
        } else {
            vocab.setFavorite(false);
        }

        holder.ivFavorite.setImageResource(R.drawable.ic_star_filled);
        holder.ivFavorite.setAlpha(vocab.isFavorite() ? 1.0f : 0.3f);

        holder.ivFavorite.setOnClickListener(v -> {
            if (!SharePrefManager.getInstance(context).isLoggedIn()) {
                Toast.makeText(context, "Login for mark as favourite", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                return;
            }

            if (vocab.isFavorite()) {
                dbHelper.removeFavorite(vocab.getId(), currentUserId);
                vocab.setFavorite(false);
            } else {
                dbHelper.addFavorite(vocab, currentUserId);
                vocab.setFavorite(true);
            }
            notifyItemChanged(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return vocabularyList != null ? vocabularyList.size() : 0;
    }

    public void updateList(List<Vocabulary> newList) {
        this.vocabularyList = newList;
        notifyDataSetChanged();
    }

}
