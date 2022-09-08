package com.zwy.ipmsg.tcp;

import com.zwy.ipmsg.Constant;
import com.zwy.ipmsg.NetOptions;
import com.zwy.ipmsg.TransferDialog;
import com.zwy.ipmsg.beans.TransferBean;
import com.zwy.ipmsg.beans.UserBean;
import com.zwy.ipmsg.utils.FileUtil;

import javax.swing.*;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class TCPClient implements Runnable {
    private final UserBean localUser;
    private final String address;
    private final File sendfile;
    private final TransferBean transferBean;


    public TCPClient(UserBean localUser, String address, File sendfile, TransferBean transferBean) {
        this.localUser = localUser;
        this.address = address;
        this.sendfile = sendfile;
        this.transferBean = transferBean;
    }

    private OutputStream ous;
    private BufferedOutputStream bOus;
    private DataOutputStream dOus;
    private InputStream ins;
    private BufferedInputStream bIns;
    private DataInputStream dIns;
    long sendedSize;
    long oldTime;
    byte[] buffer;

    @Override
    public void run() {
        Socket client = null;
        buffer = new byte[1024];
        try {
            client = new Socket(address, Constant.TCP_SERVER_PORT);

            ous = client.getOutputStream();
            bOus = new BufferedOutputStream(ous);
            dOus = new DataOutputStream(bOus);
            ins = client.getInputStream();
            bIns = new BufferedInputStream(ins);
            dIns = new DataInputStream(bIns);

            dOus.writeUTF(localUser.getUsername());
            dOus.flush();

            dOus.writeInt(NetOptions.FileOrDir);
            dOus.flush();
            dOus.writeUTF(sendfile.getName());
            dOus.flush();
            dOus.writeLong(FileUtil.getTotalSize(sendfile));
            dOus.flush();

            int confirmResp = dIns.readInt();
            if (confirmResp == NetOptions.YES) {
                System.out.println("远程服务器同意接收文件");
                TransferDialog.getInstance().showTransferDialog();
                int start = JOptionPane.showConfirmDialog(null,
                        "远程服务器同意接收文件,开始传输文件。", "开始传输", JOptionPane.YES_OPTION);
                if (start == 0) {
                    TransferDialog.getInstance().showTransferDialog();
                    sendedSize = 0;
                    oldTime = new Date().getTime();
                    sendFile(sendfile);
                }
            } else if (confirmResp == NetOptions.NO) {
                TransferDialog.getInstance().refuseRec(transferBean);
                JOptionPane.showMessageDialog(null, "对方拒绝接收文件夹！");
            }

            int result = dIns.readInt();
            if (result == NetOptions.OVER) {
                dOus.close();
                dIns.close();
                ins.close();
                JOptionPane.showMessageDialog(null, "对方已接收完毕\n" +
                        "文件名:" + sendfile.getName() + "\n" +
                        "文件大小:" + FileUtil.getFormatFileSize(FileUtil.getTotalSize(sendfile)));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(111);
            JOptionPane.showMessageDialog(null, e.getMessage() + this.getClass(), "错误", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendFile(File file) throws IOException {
        dOus.writeUTF(file.getName());
        dOus.flush();
        if (file.isFile()) {
            dOus.writeInt(NetOptions.File);
            dOus.flush();
            dOus.writeLong(file.length());
            dOus.flush();
            FileInputStream fin = new FileInputStream(file);
            int len = 0;
            while ((len = fin.read(buffer)) != -1) {
                dOus.write(buffer, 0, len);
                sendedSize += len;
                long newTime = new Date().getTime();
                transferBean.setFinishedSize(sendedSize);
                transferBean.setPercent((int) (sendedSize / 1.0f / transferBean.getTotalSize() * 100));
                transferBean.setCurrentSpeed(sendedSize / (newTime - oldTime + 1) * 1000);
                TransferDialog.getInstance().sendRepaint();
                dOus.flush();
            }
            dOus.flush();
            fin.close();

        } else if (file.isDirectory()) {
            dOus.writeInt(NetOptions.Directory);
            dOus.flush();
            File[] children = file.listFiles();
            if (children != null) {
                dOus.writeInt(children.length);
                dOus.flush();
                for (File child : children) {
                    sendFile(child);
                    int result = dIns.readInt();
                    if (result == NetOptions.OVER_ONE_FILE) {
                        continue;
                    } else {
                        break;
                    }
                }
            }
        }
    }
}
