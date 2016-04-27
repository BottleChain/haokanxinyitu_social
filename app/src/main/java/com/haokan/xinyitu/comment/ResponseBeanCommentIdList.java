package com.haokan.xinyitu.comment;

import com.haokan.xinyitu.base.BaseResponseBean;

import java.util.List;

/**
 * Created by wangzixu on 2016/4/18.
 */
public class ResponseBeanCommentIdList extends BaseResponseBean {

    /**
     * comment_id : 15
     */

    private List<CommitIdBean> data;

    public List<CommitIdBean> getData() {
        return data;
    }

    public void setData(List<CommitIdBean> data) {
        this.data = data;
    }

    public static class CommitIdBean {
        private String comment_id;

        public String getComment_id() {
            return comment_id;
        }

        public void setComment_id(String comment_id) {
            this.comment_id = comment_id;
        }
    }
}
