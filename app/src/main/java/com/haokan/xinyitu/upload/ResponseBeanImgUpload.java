package com.haokan.xinyitu.upload;

import com.haokan.xinyitu.base.BaseResponseBean;
import com.haokan.xinyitu.main.DemoTagBean;

import java.util.List;

public class ResponseBeanImgUpload extends BaseResponseBean{

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
        /**
         * image : {"image_id":"100173","image_title":"default","description":"","file_name":"145879143665328028051.jpg","create_time":"1458791436"}
         * tags : [{"id":"83879b097a78f99f","name":"测试标题2"},{"id":"11552e27ab1f0e99","name":"测试标题3"}]
         */

        private RBIUImageBean image;
        private List<DemoTagBean> tags;

        public void setImage(RBIUImageBean image) {
            this.image = image;
        }

        public void setTags(List<DemoTagBean> tags) {
            this.tags = tags;
        }

        public RBIUImageBean getImage() {
            return image;
        }

        public List<DemoTagBean> getTags() {
            return tags;
        }

        public static class RBIUImageBean {
            /**
             * image_id : 100173
             * image_title : default
             * description :
             * file_name : 145879143665328028051.jpg
             * create_time : 1458791436
             */

            private String image_id;
            private String image_title;
            private String description;
            private String file_name;
            private String create_time;

            public void setImage_id(String image_id) {
                this.image_id = image_id;
            }

            public void setImage_title(String image_title) {
                this.image_title = image_title;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public void setFile_name(String file_name) {
                this.file_name = file_name;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public String getImage_id() {
                return image_id;
            }

            public String getImage_title() {
                return image_title;
            }

            public String getDescription() {
                return description;
            }

            public String getFile_name() {
                return file_name;
            }

            public String getCreate_time() {
                return create_time;
            }
        }
    }
}
