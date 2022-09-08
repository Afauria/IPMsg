package com.zwy.ipmsg.udp;

import com.zwy.ipmsg.NetOptions;

import javax.swing.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by 赤脚和尚 on 2018/5/17.
 */
public class UDPServer implements Runnable {
    private static UDPServer udpServer;

    public static UDPServer getInstance() {
        if (udpServer == null) {
            udpServer = new UDPServer();
        }
        return udpServer;
    }

    private UDPRecCallback udpRecCallback;

    public void setUdpRecCallback(UDPRecCallback udpRecCallback) {
        this.udpRecCallback = udpRecCallback;
    }

    @Override
    public void run() {
        System.out.println("UDPServerThread:" + Thread.currentThread());
        byte[] recData = new byte[1024];
        try {
            DatagramPacket recPacket = new DatagramPacket(recData, recData.length);
            DatagramSocket udpServer = new DatagramSocket(8099);
            while (true) {
                try {
                    udpServer.receive(recPacket);
                    String data = new String(recPacket.getData(), 0, recPacket.getLength(), "UTF-8");
                    String[] ss = data.split(":");

                    int flag = Integer.parseInt(ss[0]);
                    String content = ss[1];
                    if (udpRecCallback != null) {
                        if (flag == NetOptions.Login) {
                            udpRecCallback.loginCallback(false, content, recPacket.getAddress().getHostName(),
                                    recPacket.getAddress().getHostAddress());
                        } else if (flag == NetOptions.Online) {
                            udpRecCallback.loginCallback(true, content, recPacket.getAddress().getHostName(),
                                    recPacket.getAddress().getHostAddress());
                        } else if (flag == NetOptions.Msg) {
                            udpRecCallback.sendMsgCallback(recPacket.getAddress().getHostAddress(), content);
                        } else if (flag == NetOptions.Leave) {
                            udpRecCallback.leaveCallback(content, recPacket.getAddress().getHostName(),
                                    recPacket.getAddress().getHostAddress());
                        }
                    }
                    recPacket.setLength(recData.length);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, this.getClass() + e.toString(), "错误", JOptionPane.DEFAULT_OPTION);
                }
            }
        } catch (SocketException e) {
            JOptionPane.showMessageDialog(null, this.getClass() + e.toString(), "错误", JOptionPane.DEFAULT_OPTION);
        }
    }
}
