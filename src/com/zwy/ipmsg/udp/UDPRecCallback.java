package com.zwy.ipmsg.udp;

public interface UDPRecCallback {
    void loginCallback(boolean isOnline, String username, String hostname, String address);

    void sendMsgCallback(String address, String msg);

    void leaveCallback(String username, String hostName, String address);
}
