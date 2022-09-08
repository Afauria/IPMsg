package com.zwy.ipmsg.beans;

import java.io.Serializable;

public class UserBean implements Serializable {
    private String username;
    private String hostname;
    private String address;
    private String iconUrl;
    private boolean isRecMsg;

    public boolean isRecMsg() {
        return isRecMsg;
    }

    public void setRecMsg(boolean recMsg) {
        isRecMsg = recMsg;
    }

    public UserBean(String username, String hostname, String address, String iconUrl) {
        this.username = username;
        this.hostname = hostname;
        this.address = address;
        this.iconUrl = iconUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
