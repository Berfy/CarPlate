package com.wlb.pndecoder.model.histroy;

/**
 * Created by Berfy on 2017/6/14.
 * 历史车牌Bean
 */

public class HistroyNumber {

    private String phone;//手机号
    private String number;
    private String path;
    private String updateTime;
    private int status;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
