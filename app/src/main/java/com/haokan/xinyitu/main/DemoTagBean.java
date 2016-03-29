package com.haokan.xinyitu.main;

import android.os.Parcel;
import android.os.Parcelable;

public class DemoTagBean implements Parcelable {
    private String id;
    private String name;

    //这四个属性都是用来确定图片位置的，不是服务器给的，需要自己算出
    private int marginLeft;
    private int marginTop;
    private int itemWidth;
    private int itemHeigh;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public int getItemHeigh() {
        return itemHeigh;
    }

    public void setItemHeigh(int itemHeigh) {
        this.itemHeigh = itemHeigh;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public int getItemWidth() {
        return itemWidth;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
    }

    public DemoTagBean() {
    }

    protected DemoTagBean(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<DemoTagBean> CREATOR = new Parcelable.Creator<DemoTagBean>() {
        public DemoTagBean createFromParcel(Parcel source) {
            return new DemoTagBean(source);
        }

        public DemoTagBean[] newArray(int size) {
            return new DemoTagBean[size];
        }
    };
}
