package com.zwy.ipmsg;

import com.zwy.ipmsg.beans.UserBean;

import java.util.Vector;

public class UserManager {
    private static UserManager instance;

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    private Vector<UserBean> allUser = new Vector<>();

    public Vector<UserBean> getAllUser() {
        return allUser;
    }

    public void setAllUser(Vector<UserBean> allUser) {
        this.allUser = allUser;
    }

    public UserBean getUserBeanByIp(String ip) {
        for (UserBean userBean : allUser) {
            if (ip.equals(userBean.getAddress())) {
                return userBean;
            }
        }
        return null;
    }
}
