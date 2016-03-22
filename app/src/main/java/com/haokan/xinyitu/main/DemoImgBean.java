package com.haokan.xinyitu.main;

import android.os.Parcel;
import android.os.Parcelable;

public class DemoImgBean implements Parcelable {
    private int width;
    private int heigh;
    private String path;
    private String md5; //图片对应的MD5唯一码

    //这四个属性都是用来确定图片位置的，不是服务器给的，需要自己算出
    private int marginLeft;
    private int marginTop;
    private int itemWidth;
    private int itemHeigh;

//    private boolean isChecked; //该图片是否被选中

//    public boolean isChecked() {
//        return isChecked;
//    }

//    public void setIsChecked(boolean isChecked) {
//        this.isChecked = isChecked;
//    }


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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getHeigh() {
        return heigh;
    }

    public void setHeigh(int heigh) {
        this.heigh = heigh;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.width);
        dest.writeInt(this.heigh);
        dest.writeString(this.path);
//        dest.writeByte(isChecked ? (byte) 1 : (byte) 0);
    }

    public DemoImgBean() {
    }

    protected DemoImgBean(Parcel in) {
        this.width = in.readInt();
        this.heigh = in.readInt();
        this.path = in.readString();
//        this.isChecked = in.readByte() != 0;
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
