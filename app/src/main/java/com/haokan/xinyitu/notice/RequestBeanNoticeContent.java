package com.haokan.xinyitu.notice;

import com.haokan.xinyitu.base.BaseRequestBean;

import java.util.List;

/**
 * Created by wangzixu on 2016/4/28.
 */
public class RequestBeanNoticeContent extends BaseRequestBean {

    /**
     * notice_ids : [{"id":"101"}]
     * size : 360
     */

    private int size;
    /**
     * id : 101
     */

    private List<NoticeIdsBean> notice_ids;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<NoticeIdsBean> getNotice_ids() {
        return notice_ids;
    }

    public void setNotice_ids(List<NoticeIdsBean> notice_ids) {
        this.notice_ids = notice_ids;
    }

    public static class NoticeIdsBean {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
