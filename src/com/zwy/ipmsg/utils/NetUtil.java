package com.zwy.ipmsg.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by 赤脚和尚 on 2018/5/16.
 */
public class NetUtil {
    public static InetAddress getBroadCastIP() {
        InetAddress host = null;
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String hostAddress = host.getHostAddress();
        int k = 0;

        k = hostAddress.lastIndexOf(".");
        String ss = hostAddress.substring(0, k + 1);
        String ip = ss + "255";
        InetAddress target = null;
        try {
            target = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return target;
    }
}
