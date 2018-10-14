package com.zwy.ipmsg.udp;

import com.zwy.ipmsg.Constant;
import com.zwy.ipmsg.NetOptions;

import javax.swing.*;
import java.net.*;

/**
 * Created by 赤脚和尚 on 2018/5/17.
 */
public class UDPSend implements Runnable {

    private InetAddress address;
    private int option;
    private String msg;

    public UDPSend(int option, InetAddress address, String msg) {
        this.address = address;
        this.option = option;
        this.msg = msg;
    }

    public void run() {
        System.out.println("UDPSendCThread:" + Thread.currentThread());
        String newStr = "";
        if (option == NetOptions.Login || option == NetOptions.Msg || option == NetOptions.Leave || option == NetOptions.Online) {
            newStr += option + ":";
            newStr += msg;
        } else {
            JOptionPane.showMessageDialog(null, "udp发送：没有该指令！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            byte data[] = newStr.getBytes("UTF-8");
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, Constant.UDP_SERVER_PORT);
            clientSocket.send(sendPacket);
            clientSocket.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage() + this.getClass().toString(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
