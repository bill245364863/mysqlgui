package com.bill.entity;

/**
 * @Author huangxiaotao
 * @Date 2022/11/30 22:54
 **/

public class Database {
    private String url;
    private String userName;
    private String password;

    public Database(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
