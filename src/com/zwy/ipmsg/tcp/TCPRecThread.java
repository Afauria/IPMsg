package com.zwy.ipmsg.tcp;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.zwy.ipmsg.NetOptions;
import com.zwy.ipmsg.TransferDialog;
import com.zwy.ipmsg.UserManager;
import com.zwy.ipmsg.beans.TransferBean;
import com.zwy.ipmsg.beans.UserBean;
import com.zwy.ipmsg.utils.FileUtil;
import com.zwy.ipmsg.utils.NetUtil;
import sun.nio.ch.Net;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Date;

public class TCPRecThread implements Runnable {
    private UserManager userManager = UserManager.getInstance();
    private String savePath = "Download";

    private Socket client;
    private InputStream ins;
    private BufferedInputStream bIns;
    private DataInputStream dIns;

    private OutputStream ous;
    private BufferedOutputStream bOus;
    private DataOutputStream dOus;

    private String clientName;
    private int option;
    private int receivedSize = 0;
    private long oldTime;
    private byte[] buffer;
    public TCPRecThread(Socket client) {
        this.client = client;
    }


    @Override
    public void run() {
        buffer= new byte[1024];
        try {
            ins = client.getInputStream();
            bIns = new BufferedInputStream(ins);
            dIns = new DataInputStream(bIns);
            ous = client.getOutputStream();
            bOus = new BufferedOutputStream(ous);
            dOus = new DataOutputStream(bOus);
            clientName = dIns.readUTF();
            option = dIns.readInt();
            System.out.println("收到连接请求:" + option);

            String recFileName = null;

            recFileName = dIns.readUTF();

            long totalSize = dIns.readLong();
            int confirmRec = JOptionPane.showConfirmDialog(null,
                    clientName + "给您发送文件，是否接收？\n" +
                            "文件名:" + recFileName +
                            "\n文件大小:" + FileUtil.getFormatFileSize(totalSize) + "\n",
                    "接收文件",
                    JOptionPane.YES_NO_OPTION);
            //同意接收，选择路径
            if (confirmRec == 0) {
                showFileSaveDialog();
                dOus.writeInt(NetOptions.YES);
                dOus.flush();
                transferBean = new TransferBean(recFileName, totalSize);
                TransferDialog.getInstance().addRecElement(transferBean);
                TransferDialog.getInstance().showTransferDialog();
                TransferDialog.getInstance().recUpdateUI();
            }
            oldTime = new Date().getTime();

            recFile(savePath);

            JOptionPane.showMessageDialog(null, "文件接收完毕！\n" +
                    "文件名:" + recFileName + "\n" +
                    "文件大小:" + FileUtil.getFormatFileSize(receivedSize));
            dOus.writeInt(NetOptions.OVER);
            dOus.flush();
            closeAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TransferBean transferBean;

    private void recFile(String fileDir) throws IOException{
        String name = dIns.readUTF();
        int type = dIns.readInt();
        if (type == NetOptions.File) {
            File tempFile = new File(fileDir+File.separator+name);
            FileOutputStream fOut = new FileOutputStream(tempFile);
            long fileSize=dIns.readLong();
            int len = 0;
            long singleReceivedSize=0;
            while ((len = dIns.read(buffer)) != -1) {
                fOut.write(buffer, 0, len);
                receivedSize += len;
                singleReceivedSize+=len;
                long newTime = new Date().getTime();
                transferBean.setFinishedSize(receivedSize);
                transferBean.setPercent((int) (receivedSize / 1.0f / transferBean.getTotalSize() * 100));
                transferBean.setCurrentSpeed(receivedSize / (newTime - oldTime + 1) * 1000);
                TransferDialog.getInstance().recRepaint();
                fOut.flush();
                if (singleReceivedSize >= fileSize) {
                    break;
                }
            }
            dOus.writeInt(NetOptions.OVER_ONE_FILE);
            dOus.flush();
            fOut.close();
        } else if (type == NetOptions.Directory) {
            File dir = new File(fileDir+File.separator+name);
            dir.mkdirs();
            int childCount=dIns.readInt();
            for(int i=0;i<childCount;i++){
                recFile(fileDir+File.separator+name);
            }
            dOus.writeInt(NetOptions.OVER_ONE_FILE);
            dOus.flush();
        }
    }

    void showFileSaveDialog() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("保存文件");
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setFileSelectionMode(chooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            savePath = chooser.getSelectedFile().getAbsolutePath();
        else
            JOptionPane.showMessageDialog(null, "文件将默认保存在:\r\n"
                    + new File(savePath).getAbsolutePath(), "提示", JOptionPane.DEFAULT_OPTION);
        if (!new File(savePath).exists()) {
            new File(savePath).mkdirs();
        }
    }

    void closeAll() {
        try {
            //只关闭最外层或者从外层往内层关。
            dOus.close();
            dIns.close();
            client.close();
            System.out.println("Close all");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
