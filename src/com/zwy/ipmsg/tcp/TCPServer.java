package com.zwy.ipmsg.tcp;

import com.zwy.ipmsg.Constant;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TCPServer implements Runnable {
    private static TCPServer tcpServer;

    public static TCPServer getInstance() {
        if (tcpServer == null) {
            tcpServer = new TCPServer();
        }
        return tcpServer;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        ExecutorService executor= Executors.newCachedThreadPool();
        try {
            serverSocket = new ServerSocket(Constant.TCP_SERVER_PORT);
            while (true) {
                Socket client = serverSocket.accept();
                TCPRecThread tcpRecThread = new TCPRecThread(client);
                executor.submit(tcpRecThread);
//                tcpRecThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
