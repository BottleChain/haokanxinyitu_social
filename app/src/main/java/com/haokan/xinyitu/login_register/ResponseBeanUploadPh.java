package com.haokan.xinyitu.login_register;

import com.haokan.xinyitu.base.BaseBean;

public class ResponseBeanUploadPh extends BaseBean {

    private DataBean data;

    public void setData(DataBean data) {
        this.data = data;
    }

    public DataBean getData() {
        return data;
    }

    public static class DataBean {
        /**
         * filename : sadfas
         */

        private String filename;

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }
    }
}
