package com.haokan.xinyitu.main;

public class DemoImgBean {
    private int width;
    private int heigh;
    private String path;

    //这四个属性都是用来确定图片位置的，不是服务器给的，需要自己算出
    private int marginLeft;
    private int marginTop;
    private int itemWidth;
    private int itemHeigh;

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
}
