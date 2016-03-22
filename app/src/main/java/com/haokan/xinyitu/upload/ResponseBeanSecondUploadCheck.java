package com.haokan.xinyitu.upload;

import com.haokan.xinyitu.base.BaseResponseBean;

public class ResponseBeanSecondUploadCheck extends BaseResponseBean {
//    data
//    unique_id	图片唯一码
//    file_name	服务器文件名称
//    create_time	首次上传时间
    private DataBean data;

    public void setData(DataBean data) {
        this.data = data;
    }

    public DataBean getData() {
        return data;
    }

    public static class DataBean {
        private String unique_id;
        private String file_name;
        private String create_time;

        public String getUnique_id() {
            return unique_id;
        }

        public void setUnique_id(String unique_id) {
            this.unique_id = unique_id;
        }

        public String getFile_name() {
            return file_name;
        }

        public void setFile_name(String file_name) {
            this.file_name = file_name;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }
    }
}
