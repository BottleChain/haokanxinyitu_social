package com.haokan.xinyitu.follow;

import com.haokan.xinyitu.base.BaseResponseBean;

import java.util.List;

/**
 * 作者：wangzixu on 2016/4/7 18:54
 * QQ：378320002
 */
public class ResponseBeanFollwsUsers extends BaseResponseBean {

    /**
     * data : [{"userid":1000021,"create_time":"1460025777"},{"userid":1000015,"create_time":"1460025778"}]
     */

    private List<FollowUserIdBean> data;

    public void setData(List<FollowUserIdBean> data) {
        this.data = data;
    }

    public List<FollowUserIdBean> getData() {
        return data;
    }

    public static class FollowUserIdBean {
        /**
         * userid : 1000021
         * create_time : 1460025777
         */

        private String userid;
        /**
         * 关系:1.关注,2.粉丝,3.互粉
         */
        private String relation;
        //private String create_time;


        public String getRelation() {
            return relation;
        }

        public void setRelation(String relation) {
            this.relation = relation;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getUserid() {
            return userid;
        }
    }
}
