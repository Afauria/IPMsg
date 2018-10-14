package com.zwy.ipmsg.beans;

import java.awt.*;
import java.text.DecimalFormat;

public class TransferBean {
    private String fileName;
    private long totalSize;
    private long finishedSize;
    private int percent;
    private long currentSpeed;

    public TransferBean(String fileName, long totalSize) {
        this.fileName = fileName;
        this.totalSize = totalSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getFinishedSize() {
        return finishedSize;
    }

    public void setFinishedSize(long finishedSize) {
        this.finishedSize = finishedSize;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public long getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(long currentSpeed) {
        this.currentSpeed = currentSpeed;
    }
}
