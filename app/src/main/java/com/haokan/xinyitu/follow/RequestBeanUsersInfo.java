package com.haokan.xinyitu.follow;

import com.haokan.xinyitu.base.BaseRequestBean;

import java.util.ArrayList;

/**
 * 作者：wangzixu on 2016/4/7 19:03
 * QQ：378320002
 */
public class RequestBeanUsersInfo extends BaseRequestBean {

    /**
     * user : [{"id":"101"}]
     */

    private ArrayList<UserInfoRequestEntity> user;

    public ArrayList<UserInfoRequestEntity> getUser() {
        return user;
    }

    public void setUser(ArrayList<UserInfoRequestEntity> user) {
        this.user = user;
    }

    public static class UserInfoRequestEntity {
        /**
         * id : 101
         */

        private String id;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}
