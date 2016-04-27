package com.haokan.xinyitu.comment;

import com.haokan.xinyitu.base.AvatarUrlBean;
import com.haokan.xinyitu.base.BaseResponseBean;

import java.util.List;

/**
 * Created by wangzixu on 2016/4/18.
 */
public class ResponseBeanGetCommentInfo extends BaseResponseBean {

    /**
     * comment_id : 15
     * album_id : 643
     * user_id : 1000015
     * to_user_id : 0
     * p_comment_id : 0
     * content : wwwwwwwww
     * create_time : 1460960778
     * status : 0
     * user_avatar_url : {"s50":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/92/84/145930247449737768492.jpg@!at50","s100":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/92/84/145930247449737768492.jpg@!at100","s150":"http://haokanres.img-cn-hangzhou.aliyuncs.com/avatar/92/84/145930247449737768492.jpg@!at150"}
     * user_nickname : 用户_1457504403708772
     * to_user_nickname :
     */

    private List<CommentInfoBean> data;

    public List<CommentInfoBean> getData() {
        return data;
    }

    public void setData(List<CommentInfoBean> data) {
        this.data = data;
    }

    public static class CommentInfoBean {
        private String comment_id;
        private String album_id;
        private String user_id;
        private int to_user_id;
        private int p_comment_id;
        private String content;
        private String create_time;

        private int status;

        private AvatarUrlBean user_avatar_url;
        private String user_nickname;
        private String to_user_nickname;

        public String getComment_id() {
            return comment_id;
        }

        public void setComment_id(String comment_id) {
            this.comment_id = comment_id;
        }

        public String getAlbum_id() {
            return album_id;
        }

        public void setAlbum_id(String album_id) {
            this.album_id = album_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public int getTo_user_id() {
            return to_user_id;
        }

        public void setTo_user_id(int to_user_id) {
            this.to_user_id = to_user_id;
        }

        public int getP_comment_id() {
            return p_comment_id;
        }

        public void setP_comment_id(int p_comment_id) {
            this.p_comment_id = p_comment_id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public AvatarUrlBean getUser_avatar_url() {
            return user_avatar_url;
        }

        public void setUser_avatar_url(AvatarUrlBean user_avatar_url) {
            this.user_avatar_url = user_avatar_url;
        }

        public String getUser_nickname() {
            return user_nickname;
        }

        public void setUser_nickname(String user_nickname) {
            this.user_nickname = user_nickname;
        }

        public String getTo_user_nickname() {
            return to_user_nickname;
        }

        public void setTo_user_nickname(String to_user_nickname) {
            this.to_user_nickname = to_user_nickname;
        }
    }
}
