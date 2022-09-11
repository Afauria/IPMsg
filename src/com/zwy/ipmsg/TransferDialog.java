package com.zwy.ipmsg;

import com.zwy.ipmsg.beans.TransferBean;
import com.zwy.ipmsg.models.TransferTableModel;
import com.zwy.ipmsg.rendeders.TransferTableRenderer;

import javax.swing.*;
import javax.swing.table.TableModel;

import java.awt.*;
import java.util.Vector;

public class TransferDialog {
    private static TransferDialog instance;

    public static TransferDialog getInstance() {
        if (instance == null) {
            instance = new TransferDialog();
        }
        return instance;
    }

    private JFrame transferDialog;
    private JTable sendTransferTable;
    private JTable recTransferTable;
    private final Vector<TransferBean> sendTransfer = new Vector<>();
    private final Vector<TransferBean> recTransfer = new Vector<>();
    private final TransferTableModel sendTransferTableModel = new TransferTableModel(sendTransfer, Constant.transferColumn);
    private final TransferTableModel recTransferTableModel = new TransferTableModel(recTransfer, Constant.transferColumn);

    private TransferDialog() {
    	sendTransferTable = initSendTransferTable(sendTransferTableModel);
        recTransferTable = initSendTransferTable(recTransferTableModel);
        transferDialog = new JFrame("文件传输");
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JLabel sendLabel = new JLabel("已发送");
        JLabel recLabel = new JLabel("已接收");
        sendLabel.setFont(new Font(null, Font.BOLD, 18));
        recLabel.setFont(new Font(null, Font.BOLD, 18));
        panel1.setBackground(Constant.themeColor02);
        panel2.setBackground(Constant.themeColor02);
        panel1.add(sendLabel);
        panel2.add(recLabel);
        transferDialog.setLayout(new BoxLayout(transferDialog.getContentPane(), BoxLayout.Y_AXIS));
        transferDialog.add(panel1);
        //已发送文件
        JScrollPane sendPanel = new JScrollPane(sendTransferTable);
        sendPanel.setPreferredSize(new Dimension(800, sendPanel.getPreferredSize().height - 100));
        transferDialog.add(sendPanel);

        transferDialog.add(panel2);
        //已接收文件
        JScrollPane recPanel = new JScrollPane(recTransferTable);
        recPanel.setPreferredSize(new Dimension(800, recPanel.getPreferredSize().height - 100));
        transferDialog.add(recPanel);

        transferDialog.pack();
        transferDialog.setResizable(false);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenDimen = tk.getScreenSize();
        Dimension dialogDimen = transferDialog.getContentPane().getSize();
        transferDialog.setLocation((screenDimen.width - dialogDimen.width) / 2, (screenDimen.height - dialogDimen.height) / 2);
    }

    private JTable initSendTransferTable(TableModel model) {
        JTable table = new JTable();
        table.setRowHeight(40);
        table.getTableHeader().setFont(new Font(null, Font.BOLD, 14));
        table.setFont(new Font(null, Font.PLAIN, 14));
        table.setSelectionBackground(Color.LIGHT_GRAY);
        table.setGridColor(Color.GRAY);
        table.setModel(model);

        table.setDefaultRenderer(table.getColumnClass(0), new TransferTableRenderer());
        table.getColumn(Constant.transferColumn[0]).setPreferredWidth(300);
        table.getColumn(Constant.transferColumn[1]).setPreferredWidth(200);
        table.getColumn(Constant.transferColumn[2]).setPreferredWidth(200);
        table.getColumn(Constant.transferColumn[3]).setPreferredWidth(400);
        table.getColumn(Constant.transferColumn[4]).setPreferredWidth(250);
        return table;
    }

    public void refuseRec(TransferBean transferBean) {
        transferBean.setPercent(-1);
        sendTransferTable.repaint();
    }

    public void showTransferDialog() {
        transferDialog.setVisible(true);
    }

    public void sendRepaint() {
        //需要改变数据的时候使用repaint
        sendTransferTable.repaint();
    }

    public void recRepaint() {
        recTransferTable.repaint();
    }

    public void sendUpdateUI() {
        //需要添加组件的时候使用updateUI
        sendTransferTable.updateUI();
    }

    public void recUpdateUI() {
        recTransferTable.updateUI();
    }

    public void addSendElement(TransferBean transferBean) {
        sendTransfer.addElement(transferBean);
    }

    public void hideTransferDialog() {
        transferDialog.setVisible(false);
    }

    public void addRecElement(TransferBean transferBean) {
        recTransfer.addElement(transferBean);
    }
}
