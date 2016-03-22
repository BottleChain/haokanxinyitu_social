package com.haokan.xinyitu.login_register;

import com.haokan.xinyitu.base.BaseRequestBean;

public class RequestBeanLoginSms extends BaseRequestBean {
    private String mobile;
    private String smscode;

    public String getSmscode() {
        return smscode;
    }

    public void setSmscode(String smscode) {
        this.smscode = smscode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
