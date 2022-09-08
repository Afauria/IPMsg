package com.zwy.ipmsg;

import java.awt.*;

public interface Constant {
    int TCP_SERVER_PORT = 8098;
    int UDP_SERVER_PORT = 8099;

    String appNameStr = "飞鸽传书";
    int defaultWndWidth = 960;
    int defaultWndHeight = 640;
    String help = "在输入框内按回车发送消息\n在窗口中按ctrl+s打开文件\n在线用户检测和发送消息使用UDP协议\n文件发送使用TCP协议";
    String[] menus = {"查看"};
    String[][] menuItems = {{"用户信息", "传输", "帮助"}};
    String[][] menuItemsIconURL = {{"/images/icons/user.png", "/images/icons/file.png", "/images/icons/help.png"}};

    String[] userTableColumn = {"用户名", "主机名", "IP地址"};
    String[] transferColumn = {"文件名", "总大小", "已传输", "进度", "当前速度"};
    String HINT_NULL_MSG = "消息不能为空";
    String HINT_NULL_REMOTE_USER = "请指定目标用户";
    String HINT_BIND_LOCAL_ERROR = "获取本机用户失败";

    Color themeColor = new Color(248, 244, 241);
    Color themeColorSelect = new Color(228, 224, 221);
    Color themeColorPressed = new Color(208, 192, 177);

    Color themeColor01 = new Color(238, 234, 231);
    Color themeColor02 = new Color(213, 203, 199);
    Color themeColor03 = new Color(187, 170, 163);
    Color themeColor04 = new Color(159, 135, 126);
    Color themeColor05 = new Color(139, 110, 99);
    Color themeColor06 = new Color(121, 85, 71);
    Color themeColor07 = new Color(109, 77, 66);
    String[] iconURL = {"/images/users/user1.png", "/images/users/user2.png",
            "/images/users/user3.png", "/images/users/user4.png",
            "/images/users/user5.png", "/images/users/user6.png",
            "/images/users/user7.png", "/images/users/user8.png",
            "/images/users/user9.png", "/images/users/user10.png",
            "/images/users/user11.png", "/images/users/user12.png",
            "/images/users/user13.png", "/images/users/user14.png",
            "/images/users/user15.png", "/images/users/user16.png",
            "/images/users/user17.png", "/images/users/user18.png",
            "/images/users/user19.png", "/images/users/user20.png"};
}
