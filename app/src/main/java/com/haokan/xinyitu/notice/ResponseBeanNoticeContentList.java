package com.haokan.xinyitu.notice;

import com.haokan.xinyitu.base.BaseResponseBean;

import java.util.List;

/**
 * Created by wangzixu on 2016/4/28.
 */
public class ResponseBeanNoticeContentList extends BaseResponseBean {

    /**
     * type : 4
     * text : 用户_1461657327378424 刚刚关注了你
     * timestamp : 1461809235
     */

    private List<NoticeContentBean> data;

    public List<NoticeContentBean> getData() {
        return data;
    }

    public void setData(List<NoticeContentBean> data) {
        this.data = data;
    }

    public static class NoticeContentBean {
        private int type;
        private String text;
        private long timestamp;
        private String album_id;
        private String img;
        private String comment_text;
        private String pcomment;

        public String getAlbum_id() {
            return album_id;
        }

        public void setAlbum_id(String album_id) {
            this.album_id = album_id;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getComment_text() {
            return comment_text;
        }

        public void setComment_text(String comment_text) {
            this.comment_text = comment_text;
        }

        public String getPcomment() {
            return pcomment;
        }

        public void setPcomment(String pcomment) {
            this.pcomment = pcomment;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
