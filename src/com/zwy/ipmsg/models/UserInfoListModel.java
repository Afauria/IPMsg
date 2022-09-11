package com.zwy.ipmsg.models;

import com.zwy.ipmsg.beans.UserBean;

import javax.swing.*;
import javax.swing.event.ListDataListener;

import java.util.Vector;

public class UserInfoListModel implements ListModel<UserBean> {
    private Vector<UserBean> list;

    public UserInfoListModel(Vector<UserBean> list) {
        this.list = list;
    }

    public Vector<UserBean> getList() {
        return list;
    }

    public void setList(Vector<UserBean> list) {
        this.list = list;
    }

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public UserBean getElementAt(int index) {
        return list.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {

    }

    @Override
    public void removeListDataListener(ListDataListener l) {

    }
}
