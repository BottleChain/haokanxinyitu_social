package com.haokan.xinyitu.setting;

import com.haokan.xinyitu.base.BaseRequestBean;

/**
 * Created by wangzixu on 2016/4/21.
 */
public class RequestBeanChangePsw extends BaseRequestBean {
    private String OldPasswd;
    private String NewPasswd;

    public String getOldPasswd() {
        return OldPasswd;
    }

    public void setOldPasswd(String oldPasswd) {
        OldPasswd = oldPasswd;
    }

    public String getNewPasswd() {
        return NewPasswd;
    }

    public void setNewPasswd(String newPasswd) {
        NewPasswd = newPasswd;
    }
}
