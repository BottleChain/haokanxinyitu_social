package com.haokan.xinyitu.upload;

import com.haokan.xinyitu.base.BaseResponseBean;

public class ResponseBeanCreateAblum extends BaseResponseBean{

    /**
     * data : {"image":{"image_id":"100173","image_title":"default","description":"","file_name":"145879143665328028051.jpg","create_time":"1458791436"},"tags":[{"id":"83879b097a78f99f","name":"测试标题2"},{"id":"11552e27ab1f0e99","name":"测试标题3"}]}
     */

    private DataBean data;

    public void setData(DataBean data) {
        this.data = data;
    }

    public DataBean getData() {
        return data;
    }

    public static class DataBean {
        private String 	album_id;

        public String getAlbum_id() {
            return album_id;
        }

        public void setAlbum_id(String album_id) {
            this.album_id = album_id;
        }
    }
}
