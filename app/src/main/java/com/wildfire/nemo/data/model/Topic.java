package com.wildfire.nemo.data.model;

import com.google.gson.annotations.SerializedName;

public class Topic {
    @SerializedName("id")
    private int id;
    @SerializedName("name_en")
    private String nameEn;
    @SerializedName("name_vi")
    private String nameVi;
    @SerializedName("description_en")
    private String descriptionEn;
    @SerializedName("description_vi")
    private String descriptionVi;
    @SerializedName("image")
    private String image;

    public Topic() {
    }

    public Topic(int id, String nameEn, String nameVi, String descriptionEn, String descriptionVi, String image) {
        this.id = id;
        this.nameEn = nameEn;
        this.nameVi = nameVi;
        this.descriptionEn = descriptionEn;
        this.descriptionVi = descriptionVi;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameVi() {
        return nameVi;
    }

    public void setNameVi(String nameVi) {
        this.nameVi = nameVi;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
