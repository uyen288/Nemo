package com.wildfire.nemo.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Grammar implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("title_en")
    private String titleEn;
    @SerializedName("title_vi")
    private String titleVi;
    @SerializedName("description_en")
    private String descriptionEn;
    @SerializedName("description_vi")
    private String descriptionVi;
    @SerializedName("content_en")
    private String contentEn;
    @SerializedName("content_vi")
    private String contentVi;

    public Grammar() {
    }

    public Grammar(int id, String titleEn, String titleVi, String descriptionEn, String descriptionVi, String contentEn, String contentVi) {
        this.id = id;
        this.titleEn = titleEn;
        this.titleVi = titleVi;
        this.descriptionEn = descriptionEn;
        this.descriptionVi = descriptionVi;
        this.contentEn = contentEn;
        this.contentVi = contentVi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getTitleVi() {
        return titleVi;
    }

    public void setTitleVi(String titleVi) {
        this.titleVi = titleVi;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionVi() {
        return descriptionVi;
    }

    public void setDescriptionVi(String descriptionVi) {
        this.descriptionVi = descriptionVi;
    }

    public String getContentEn() {
        return contentEn;
    }

    public void setContentEn(String contentEn) {
        this.contentEn = contentEn;
    }

    public String getContentVi() {
        return contentVi;
    }

    public void setContentVi(String contentVi) {
        this.contentVi = contentVi;
    }

    // Helper methods to get localized content
    public String getLocalizedTitle(String langCode) {
        return "vi".equalsIgnoreCase(langCode) ? titleVi : titleEn;
    }

    public String getLocalizedDescription(String langCode) {
        return "vi".equalsIgnoreCase(langCode) ? descriptionVi : descriptionEn;
    }

    public String getLocalizedContent(String langCode) {
        return "vi".equalsIgnoreCase(langCode) ? contentVi : contentEn;
    }
}
