package com.haokan.xinyitu.main;

public class DemoTagBean {
    private String name;

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
}
