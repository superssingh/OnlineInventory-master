package com.santossingh.onlineinventory.Models;

/**
 * Created by santoshsingh on 04/02/17.
 */

public class Sellers {
    private String username,mobile, password;

    private String key;

    public Sellers() {
    }

    public Sellers(String username, String mobile, String password) {
        this.username = username;
        this.mobile = mobile;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
