package com.zwy.ipmsg.models;

import com.zwy.ipmsg.beans.RecordItemEntity;

import javax.swing.*;

import java.util.Vector;

/**
 * Created by 赤脚和尚 on 2018/3/28.
 */
public class RecordListModel extends AbstractListModel {
    Vector<RecordItemEntity> list;

    public RecordListModel(Vector<RecordItemEntity> list) {
        this.list = list;
    }

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public Object getElementAt(int index) {
        return list.get(index);
    }
}
