package com.haokan.xinyitu.upload;

import com.haokan.xinyitu.base.BaseResponseBean;

public class ResponseBeanSecondUploadCheck extends BaseResponseBean{

    /**
     * data : {"unique_id":"331a82f0e3de4b93e3ff0b1136a52d6c","file_name":"145879326106580562940.jpg","create_time":"1458793261"}
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
         * unique_id : 331a82f0e3de4b93e3ff0b1136a52d6c
         * file_name : 145879326106580562940.jpg
         * create_time : 1458793261
         */

        private String unique_id;
        private String file_name;
        private String create_time;

        public void setUnique_id(String unique_id) {
            this.unique_id = unique_id;
        }

        public void setFile_name(String file_name) {
            this.file_name = file_name;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getUnique_id() {
            return unique_id;
        }

        public String getFile_name() {
            return file_name;
        }

        public String getCreate_time() {
            return create_time;
        }
    }
}
