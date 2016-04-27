package com.haokan.xinyitu.follow;

import com.haokan.xinyitu.base.BaseResponseBean;
import com.haokan.xinyitu.base.UserInfoBean;

import java.util.ArrayList;

public class ResponseBeanOtherUserInfo extends BaseResponseBean {

    /**
     * data : {"userid":"1000016","nickname":"用户_1457510026808822","mobile":"18516891181","avatar":"145941496395155549864.jpeg","createtime":"1457510026","avatar_url":{"s50":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/64/98/145941496395155549864.jpeg@!at50","s100":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/64/98/145941496395155549864.jpeg@!at100","s150":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/64/98/145941496395155549864.jpeg@!at150"}}
     */

    private ArrayList<FollowUserInfoBean> data;

    public ArrayList<FollowUserInfoBean> getData() {
        return data;
    }

    public void setData(ArrayList<FollowUserInfoBean> data) {
        this.data = data;
    }

    public static class FollowUserInfoBean extends UserInfoBean { //比正常的用户信息bean多了相互关注关系
        private String relation;

        public String getRelation() {
            return relation;
        }

        public void setRelation(String relation) {
            this.relation = relation;
        }
    }
}
