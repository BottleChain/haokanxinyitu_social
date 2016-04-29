package com.haokan.xinyitu.notice;

import com.haokan.xinyitu.base.BaseResponseBean;

import java.util.List;

/**
 * Created by wangzixu on 2016/4/28.
 */
public class ResponseBeanNoticeIdList extends BaseResponseBean {

    /**
     * message_id : 56
     */

    private List<NoTiceIdBean> data;

    public List<NoTiceIdBean> getData() {
        return data;
    }

    public void setData(List<NoTiceIdBean> data) {
        this.data = data;
    }

    public static class NoTiceIdBean {
        private String message_id;

        public String getMessage_id() {
            return message_id;
        }

        public void setMessage_id(String message_id) {
            this.message_id = message_id;
        }
    }
}
