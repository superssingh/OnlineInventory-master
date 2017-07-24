package com.santossingh.onlineinventory.Models;

/**
 * Created by santoshsingh on 12/02/17.
 */

public class Admins {
    String name, mobile, password, key;

    public Admins() {
    }

    public Admins(String name, String mobile, String password) {
        this.name = name;
        this.mobile = mobile;
        this.password = password;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValues(Admins values) {
        name = values.getName();
        mobile = values.getMobile();
        password = values.getPassword();
    }
}
