package com.haokan.xinyitu.main.mypersonalcenter;

import com.haokan.xinyitu.base.AvatarUrlBean;
import com.haokan.xinyitu.base.BaseResponseBean;

public class ResponseBeanUserInfo extends BaseResponseBean {

    /**
     * data : {"userid":"1000016","nickname":"用户_1457510026808822","mobile":"18516891181","avatar":"145941496395155549864.jpeg","createtime":"1457510026","avatar_url":{"s50":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/64/98/145941496395155549864.jpeg@!at50","s100":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/64/98/145941496395155549864.jpeg@!at100","s150":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/64/98/145941496395155549864.jpeg@!at150"}}
     */

    private DataEntity data;

    public void setData(DataEntity data) {
        this.data = data;
    }

    public DataEntity getData() {
        return data;
    }

    public static class DataEntity {
        /**
         * userid : 1000016
         * nickname : 用户_1457510026808822
         * mobile : 18516891181
         * avatar : 145941496395155549864.jpeg
         * createtime : 1457510026
         * avatar_url : {"s50":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/64/98/145941496395155549864.jpeg@!at50","s100":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/64/98/145941496395155549864.jpeg@!at100","s150":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/64/98/145941496395155549864.jpeg@!at150"}
         */

        private String userid;
        private String nickname;
        private String mobile;
        private String avatar;
        private String createtime;
        private AvatarUrlBean avatar_url;

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public void setAvatar_url(AvatarUrlBean avatar_url) {
            this.avatar_url = avatar_url;
        }

        public String getUserid() {
            return userid;
        }

        public String getNickname() {
            return nickname;
        }

        public String getMobile() {
            return mobile;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getCreatetime() {
            return createtime;
        }

        public AvatarUrlBean getAvatar_url() {
            return avatar_url;
        }
    }
}
