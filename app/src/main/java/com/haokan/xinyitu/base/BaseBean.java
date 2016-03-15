package com.haokan.xinyitu.base;

public class BaseBean {

    /**
     * err_code : 1
     * err_msg : 手机号规则错误
     * ip : 192.168.0.60
     * time : 1457491529
     */

    private int err_code;
    private String err_msg;
    private String ip;
    private int time;

    public void setErr_code(int err_code) {
        this.err_code = err_code;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getErr_code() {
        return err_code;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public String getIp() {
        return ip;
    }

    public int getTime() {
        return time;
    }
}
