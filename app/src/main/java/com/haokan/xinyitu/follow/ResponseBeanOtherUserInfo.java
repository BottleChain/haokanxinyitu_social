package com.haokan.xinyitu.follow;

import com.haokan.xinyitu.base.BaseResponseBean;
import com.haokan.xinyitu.base.UserInfoBean;

import java.util.ArrayList;

public class ResponseBeanOtherUserInfo extends BaseResponseBean {

    /**
     * data : {"userid":"1000016","nickname":"用户_1457510026808822","mobile":"18516891181","avatar":"145941496395155549864.jpeg","createtime":"1457510026","avatar_url":{"s50":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/64/98/145941496395155549864.jpeg@!at50","s100":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/64/98/145941496395155549864.jpeg@!at100","s150":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/64/98/145941496395155549864.jpeg@!at150"}}
     */

    private ArrayList<UserInfoBean> data;

    public ArrayList<UserInfoBean> getData() {
        return data;
    }

    public void setData(ArrayList<UserInfoBean> data) {
        this.data = data;
    }
}
