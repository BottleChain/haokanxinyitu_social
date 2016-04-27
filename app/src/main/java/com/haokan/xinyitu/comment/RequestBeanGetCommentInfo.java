package com.haokan.xinyitu.comment;

import com.haokan.xinyitu.base.BaseRequestBean;

import java.util.List;

/**
 * Created by wangzixu on 2016/4/18.
 */
public class RequestBeanGetCommentInfo extends BaseRequestBean {
    private String album_id;
    private List<String> comment_id;

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public List<String> getComment_id() {
        return comment_id;
    }

    public void setComment_id(List<String> comment_id) {
        this.comment_id = comment_id;
    }
}
