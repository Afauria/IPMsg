package com.zwy.ipmsg.models;

import com.zwy.ipmsg.beans.TransferBean;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import java.util.Vector;

public class TransferTableModel implements TableModel {
    Vector<TransferBean> rowData;
    Object[] columnNames;

    public TransferTableModel(Vector<TransferBean> rowData, Object[] columnNames) {
        this.rowData = rowData;
        this.columnNames = columnNames;
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
            return rowData.get(rowIndex).getFileName();
        } else if (columnIndex == 1) {
            return rowData.get(rowIndex).getTotalSize();
        } else if (columnIndex == 2) {
            return rowData.get(rowIndex).getFinishedSize();
        } else if (columnIndex == 3) {
            return rowData.get(rowIndex).getPercent();
        } else if (columnIndex == 4) {
            return rowData.get(rowIndex).getCurrentSpeed();
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
