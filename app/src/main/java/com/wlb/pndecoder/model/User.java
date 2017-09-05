package com.wlb.pndecoder.model;

/**
 * Created by Berfy on 2017/6/15.
 * 用户Bean
 */
public class User {

    private String phone;
    private String name;
    private String updateTime;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
