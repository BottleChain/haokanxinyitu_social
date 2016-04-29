package com.haokan.xinyitu.notice;

import com.haokan.xinyitu.base.BaseResponseBean;

/**
 * Created by wangzixu on 2016/4/28.
 */
public class ResponseBeanLastestNotice extends BaseResponseBean {

    /**
     * count : 33
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
