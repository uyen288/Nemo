package com.wildfire.nemo.data.model;

import com.google.gson.annotations.SerializedName;
public class Vocabulary {
    @SerializedName("id")
    private int id;
    @SerializedName("word")
    private String word;
    @SerializedName("phonetic")
    private String phonetic;
    @SerializedName("meaning_en")
    private String meaningEn;
    @SerializedName("meaning_vi")
    private String meaningVi;
    @SerializedName("example_es")
    private String exampleEs;
    @SerializedName("example_en")
    private String exampleEn;
    @SerializedName("example_vi")
    private String exampleVi;
    @SerializedName("image")
    private String image;
    @SerializedName("audio")
    private String audio;
    @SerializedName("topic_id")
    private int topicId;
    @SerializedName("level")
    private String level;
    @SerializedName("is_favourite")
    private boolean isFavorite;

    public Vocabulary() {
    }

    public Vocabulary(int id, String word, String phonetic, String meaningEn, String meaningVi, String exampleEs, String exampleEn, String exampleVi, String image, String audio, int topicId, String level, boolean isFavorite) {
        this.id = id;
        this.word = word;
        this.phonetic = phonetic;
        this.meaningEn = meaningEn;
        this.meaningVi = meaningVi;
        this.exampleEs = exampleEs;
        this.exampleEn = exampleEn;
        this.exampleVi = exampleVi;
        this.image = image;
        this.audio = audio;
        this.topicId = topicId;
        this.level = level;
        this.isFavorite = isFavorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public String getMeaningEn() {
        return meaningEn;
    }

    public void setMeaningEn(String meaningEn) {
        this.meaningEn = meaningEn;
    }

    public String getMeaningVi() {
        return meaningVi;
    }

    public void setMeaningVi(String meaningVi) {
        this.meaningVi = meaningVi;
    }

    public String getExampleEs() {
        return exampleEs;
    }

    public void setExampleEs(String exampleEs) {
        this.exampleEs = exampleEs;
    }

    public String getExampleEn() {
        return exampleEn;
    }

    public void setExampleEn(String exampleEn) {
        this.exampleEn = exampleEn;
    }

    public String getExampleVi() {
        return exampleVi;
    }

    public void setExampleVi(String exampleVi) {
        this.exampleVi = exampleVi;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
