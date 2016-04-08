package com.haokan.xinyitu.follow;

import com.haokan.xinyitu.base.BaseResponseBean;

import java.util.List;

/**
 * 作者：wangzixu on 2016/4/7 18:54
 * QQ：378320002
 */
public class ResponseBeanFollwsUsersId extends BaseResponseBean {

    /**
     * data : [{"userid":1000021,"create_time":"1460025777"},{"userid":1000015,"create_time":"1460025778"}]
     */

    private List<UserIdEntity> data;

    public void setData(List<UserIdEntity> data) {
        this.data = data;
    }

    public List<UserIdEntity> getData() {
        return data;
    }

    public static class UserIdEntity {
        /**
         * userid : 1000021
         * create_time : 1460025777
         */

        private int userid;
        private String create_time;

        public void setUserid(int userid) {
            this.userid = userid;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public int getUserid() {
            return userid;
        }

        public String getCreate_time() {
            return create_time;
        }
    }
}
