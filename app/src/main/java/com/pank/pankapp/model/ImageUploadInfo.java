package com.pank.pankapp.model;

import java.io.Serializable;

public class ImageUploadInfo implements Serializable {

    public String imageName;
    public String imageURL;

    public ImageUploadInfo() {
    }

    public ImageUploadInfo(String url) {
        this.imageURL = url;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageURL() {
        return imageURL;
    }

}