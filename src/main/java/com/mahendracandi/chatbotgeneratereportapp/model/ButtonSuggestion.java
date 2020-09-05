package com.mahendracandi.chatbotgeneratereportapp.model;

import java.util.List;

public class ButtonSuggestion {
    private String pictureLink;
    private String picturePath;
    private String title;
    private String subTitle;
    private List<ButtonValues> buttonValues;

    public String getPictureLink() {
        return pictureLink;
    }

    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public List<ButtonValues> getButtonValues() {
        return buttonValues;
    }

    public void setButtonValues(List<ButtonValues> buttonValues) {
        this.buttonValues = buttonValues;
    }

    @Override
    public String toString() {
        return "ButtonSuggestion{" +
                "pictureLink='" + pictureLink + '\'' +
                ", picturePath='" + picturePath + '\'' +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", buttonValues=" + buttonValues +
                '}';
    }
}
