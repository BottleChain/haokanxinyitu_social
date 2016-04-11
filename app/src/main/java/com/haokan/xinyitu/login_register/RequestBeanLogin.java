package com.haokan.xinyitu.login_register;

import com.haokan.xinyitu.base.BaseRequestBean;

public class RequestBeanLogin extends BaseRequestBean {
    private String mobile;
    private String passwd;

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
