package com.haokan.xinyitu.main;

import android.os.Parcel;
import android.os.Parcelable;

public class DemoImgBean implements Parcelable {
    private String id;
    private String name;
    private String url;

    private String md5; //图片对应的MD5唯一码

    private String width;
    private String height;
    //这四个属性都是用来确定图片位置的，不是服务器给的，需要自己算出
    private int marginLeft;
    private int marginTop;
    private int itemWidth;
    private int itemHeigh;

    private boolean upLoadFailed; //图片上传接口上，此张图上传失败了

    public boolean isUpLoadFailed() {
        return upLoadFailed;
    }

    public void setUpLoadFailed(boolean upLoadFailed) {
        this.upLoadFailed = upLoadFailed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public int getItemHeigh() {
        return itemHeigh;
    }

    public void setItemHeigh(int itemHeigh) {
        this.itemHeigh = itemHeigh;
    }

    public int getItemWidth() {
        return itemWidth;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.url);
        dest.writeString(this.width);
        dest.writeString(this.height);
    }

    public DemoImgBean() {
    }

    protected DemoImgBean(Parcel in) {
        this.id = in.readString();
        this.url = in.readString();
        this.width = in.readString();
        this.height = in.readString();
    }

    public static final Parcelable.Creator<DemoImgBean> CREATOR = new Parcelable.Creator<DemoImgBean>() {
        public DemoImgBean createFromParcel(Parcel source) {
            return new DemoImgBean(source);
        }

        public DemoImgBean[] newArray(int size) {
            return new DemoImgBean[size];
        }
    };
}
