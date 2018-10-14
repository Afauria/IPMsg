package com.zwy.ipmsg.beans;

/**
 * Created by 赤脚和尚 on 2018/3/28.
 */
public class RecordItemEntity {
    private String name;
    private String time;
    private String content;
    private String iconUrl;
    private boolean isMine;

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public RecordItemEntity(String name, String time, String content, String iconUrl, boolean isMine) {
        this.name = name;
        this.time = time;
        this.content = content;
        this.iconUrl = iconUrl;
        this.isMine = isMine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
