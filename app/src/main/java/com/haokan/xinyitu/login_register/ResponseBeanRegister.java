package com.haokan.xinyitu.login_register;

import com.haokan.xinyitu.base.BaseResponseBean;

public class ResponseBeanRegister extends BaseResponseBean {

    /**
     * data : {"UserId":"1000016","SessionId":"01o485uhj7n9vumu0o0uqjjen7"}
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
         * UserId : 1000016
         * SessionId : 01o485uhj7n9vumu0o0uqjjen7
         */

        private String UserId;
        private String SessionId;

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }

        public void setSessionId(String SessionId) {
            this.SessionId = SessionId;
        }

        public String getUserId() {
            return UserId;
        }

        public String getSessionId() {
            return SessionId;
        }
    }
}
