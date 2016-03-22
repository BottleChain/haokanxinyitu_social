package com.haokan.xinyitu.main.discovery;

import com.haokan.xinyitu.main.DemoImgBean;
import com.haokan.xinyitu.main.DemoTagBean;

import java.util.ArrayList;

public class ResponseBeanDiscovery {
    private ArrayList<DemoImgBean> mImgs;
    private ArrayList<DemoTagBean> mTags;
    private int type;

    public ArrayList<DemoTagBean> getTags() {
        return mTags;
    }

    public void setTags(ArrayList<DemoTagBean> tags) {
        mTags = tags;
    }

    public ArrayList<DemoImgBean> getImgs() {
        return mImgs;
    }

    public void setImgs(ArrayList<DemoImgBean> imgs) {
        mImgs = imgs;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
