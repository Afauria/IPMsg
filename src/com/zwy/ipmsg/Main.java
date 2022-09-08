package com.zwy.ipmsg;

import com.zwy.ipmsg.tcp.TCPServer;
import com.zwy.ipmsg.udp.UDPSend;
import com.zwy.ipmsg.udp.UDPServer;
import com.zwy.ipmsg.utils.NetUtil;

import javax.swing.*;

import java.awt.*;
import java.net.*;

public class Main {

    public static void main(String[] args) {
        if (isOccupy()) {
            JOptionPane.showMessageDialog(null, "端口已被占用", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("MainThread;" + Thread.currentThread());

        //不使用这个方法用户列表初始化不显示????需要点击才能显示
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                //开启udp服务器线程，用于接收udp数据包，端口号为Constant.UDP_SERVER_PORT
                UDPServer udpServer = UDPServer.getInstance();
                Thread udpServerThread = new Thread(udpServer);
                udpServerThread.start();

                //开启Tcp服务器线程，用于接收文件
                TCPServer tcpServer = new TCPServer().getInstance();
                Thread tcpServerThread = new Thread(tcpServer);
                tcpServerThread.start();

                //UI初始化，注册udp监听器，用于处理udp消息及时更新UI
                MainWnd mainWnd = MainWnd.getInstance();
                mainWnd.init();
                TransferDialog transferDialog = TransferDialog.getInstance();
                notifyOnline(NetOptions.Login, null);
            }
        });

    }

    public static void notifyOnline(int state, String address) {
        InetAddress target = null;
        //向局域网发送广播，通知上线
        if (address == null) {
            target = NetUtil.getBroadCastIP();
        } else {
            //向上线用户发送回复，通知自己在线，让用户获取当前在线用户信息
            try {
                target = InetAddress.getByName(address);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        Thread online = new Thread(new UDPSend(state, target, System.getProperty("user.name")));
        online.start();
    }

    public static void notifyOffline() {
        InetAddress target = null;
        target = NetUtil.getBroadCastIP();

        Thread offline = new Thread(new UDPSend(NetOptions.Leave, target, System.getProperty("user.name")));
        offline.start();
    }

    public static boolean isOccupy() {
        try {
            ServerSocket sock = new ServerSocket(Constant.TCP_SERVER_PORT);
            sock.close();
        } catch (BindException e) {
            e.printStackTrace();
            return true;
        } catch (Exception e) {
            return true;
        }
        return false;
    }

}
