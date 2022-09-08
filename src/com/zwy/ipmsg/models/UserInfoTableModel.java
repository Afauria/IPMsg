package com.zwy.ipmsg.models;

import com.zwy.ipmsg.beans.UserBean;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import java.util.Vector;

/**
 * Created by 赤脚和尚 on 2018/3/29.
 */
public class UserInfoTableModel implements TableModel {
    private Vector<UserBean> rowData;
    private final Object[] columnNames;

    public Vector<UserBean> getRowData() {
        return rowData;
    }

    public void setRowData(Vector<UserBean> rowData) {
        this.rowData = rowData;
    }

    public UserInfoTableModel(Vector<UserBean> rowData, Object[] columnNames) {
        this.rowData = rowData;
        this.columnNames = columnNames;
    }

    public void addRow(UserBean userBean) {
        rowData.addElement(userBean);
    }

    @Override
    public int getRowCount() {
        return rowData.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex].toString();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnNames[columnIndex].getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return rowData.get(rowIndex).getUsername();
        } else if (columnIndex == 1) {
            return rowData.get(rowIndex).getHostname();
        } else if (columnIndex == 2) {
            return rowData.get(rowIndex).getAddress();
        }
        return "";
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }
}
