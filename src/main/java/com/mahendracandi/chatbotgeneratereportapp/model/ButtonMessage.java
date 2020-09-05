package com.mahendracandi.chatbotgeneratereportapp.model;

import java.util.List;

/**
 * Button Class<br/>
 * There is two type of button, where each button has different json form <br/>
 * 1. button that chatbot trainer create<br/>
 * <pre>
 * {'id':'f5e2c99255ae62ed2cbe4cf22d7bfee8','pictureLink':'{'bucket':'media-images-button',
 * 'filename':'EAB89F3C6B16D7F6A8621E6A2E5582F9_5.jpg','contentType':'image/jpeg'}','title':'PRODUK ',
 * 'subTitle':'Produk FIFGROUP','buttonValues':[{'name':'Info Layanan','value':'layanan'},{'name':'Seputar Kredit',
 * 'value':'seputar credit'},{'name':'Network','value':'network'}],'group':'1.PRODUK'}
 * </pre>
 * <br/>
 * 2. button suggestion (button from bot suggestion feature)<br/>
 * <pre>
 * {'pictureLink':'http://imagizer.imageshack.us/a/img924/2483/8Mne6U.jpg',
 * 'picturePath':'http://imagizer.imageshack.us/a/img924/2483/8Mne6U.jpg','title':'Kantor dimana',
 * 'subTitle':'Klik tombol berikut untuk mengetahui informasi ini','buttonValues':[{'name':'Tanya',
 * 'value':'Kantor dimana'}]}
 * </pre>
 *
 *  So, we need custom deserialization for attribute <b>pictureLink</b> to handle different value
 *
 *
 *
 */
public class ButtonMessage{
    private String id;
    private Object pictureLink;
    private String title;
    private String subTitle;
    private List<ButtonValues> buttonValues;
    private String group;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getPictureLink() {
        return pictureLink;
    }

    public void setPictureLink(Object pictureLink) {
        this.pictureLink = pictureLink;
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "ButtonMessage{" +
                "id='" + id + '\'' +
                ", pictureLink=" + pictureLink +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", buttonValues=" + buttonValues +
                ", group='" + group + '\'' +
                '}';
    }
}
