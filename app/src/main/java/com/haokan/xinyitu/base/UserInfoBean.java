package com.haokan.xinyitu.base;

/**
 * 作者：wangzixu on 2016/4/7 19:11
 * QQ：378320002
 */
public class UserInfoBean {
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
    private String ilikenum;
    private String likemenum;
    private String albumnum;
    private String createtime;
    private String description;
    private AvatarUrlBean avatar_url;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIlikenum() {
        return ilikenum;
    }

    public void setIlikenum(String ilikenum) {
        this.ilikenum = ilikenum;
    }

    public String getLikemenum() {
        return likemenum;
    }

    public void setLikemenum(String likemenum) {
        this.likemenum = likemenum;
    }

    public String getAlbumnum() {
        return albumnum;
    }

    public void setAlbumnum(String albumnum) {
        this.albumnum = albumnum;
    }

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
