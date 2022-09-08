package com.zwy.ipmsg;

import com.zwy.ipmsg.beans.RecordItemEntity;
import com.zwy.ipmsg.beans.TransferBean;
import com.zwy.ipmsg.beans.UserBean;
import com.zwy.ipmsg.models.RecordListModel;
import com.zwy.ipmsg.models.UserInfoListModel;
import com.zwy.ipmsg.models.UserInfoTableModel;
import com.zwy.ipmsg.rendeders.RecordListRenderer;
import com.zwy.ipmsg.rendeders.UserInfoListRenderer;
import com.zwy.ipmsg.tcp.TCPClient;
import com.zwy.ipmsg.udp.UDPRecCallback;
import com.zwy.ipmsg.udp.UDPSend;
import com.zwy.ipmsg.udp.UDPServer;
import com.zwy.ipmsg.utils.FileUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainWnd {
    private final UDPServer udpServer = UDPServer.getInstance();
    private final RecordSave recordSave = RecordSave.getInstance();
    private final UserManager userManager = UserManager.getInstance();
    private static MainWnd mainWnd;

    public static MainWnd getInstance() {
        if (mainWnd == null) {
            mainWnd = new MainWnd();
        }
        return mainWnd;
    }

    private JFrame mainFrame;

    private JPanel listPanel;
    private JPanel msgPanel;
    private JPanel chatPanel;
    private JDialog userInfoDialog;

    private JTable userInfoTable;
    private JList<RecordItemEntity> recordList;
    private JList<UserBean> userList;
    private JLabel onlineNum;
    private final Map<String, String[]> menus = new HashMap<>();

    private JTextArea inputArea;


    private final Vector<UserBean> allUsers = userManager.getAllUser();
    private Vector<RecordItemEntity> currentRecords = new Vector<>();
    private final UserInfoTableModel userInfoTableModel = new UserInfoTableModel(allUsers, Constant.userTableColumn);
    private final UserInfoListModel userInfoListModel = new UserInfoListModel(allUsers);
    private final RecordListModel recordListModel = new RecordListModel(currentRecords);

    private UserBean localUser;
    private UserBean remoteUser;

    public void init() {
        mainFrame = new JFrame(Constant.appNameStr);
        mainFrame.setIconImage(new ImageIcon(Main.class.getResource("/images/icon.png")).getImage());

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(mainFrame);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        mainFrame.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.notifyOffline();
                System.exit(0);
            }

            @Override
            public void windowOpened(WindowEvent e) {
                // TODO Auto-generated method stub
                initStartPos();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowIconified(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowActivated(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                // TODO Auto-generated method stub

            }
        });

        mainFrame.pack();
        mainFrame.setSize(Constant.defaultWndWidth, Constant.defaultWndHeight);

        initMenu();
        initUI();
        initUserInfoDialog();
        initLocalUser();
        mainFrame.setVisible(true);
        System.out.println("MainWndThread:" + Thread.currentThread());

        udpServer.setUdpRecCallback(new UDPRecCallback() {
            @Override
            public void loginCallback(boolean isOnline, String username, String hostname, String address) {
                System.out.println("UdpCallbackThread:" + Thread.currentThread());

                UserBean userBean = new UserBean(username, hostname, address, Constant.iconURL[allUsers.size() % 20]);
                addOnlineUser(userBean);
                initLocalUser();
                //局域网所有的服务器接收到登录消息后，回复自己在线的广播包，让登录的用户知道自己在线
                if (!isOnline) {
                    Main.notifyOnline(NetOptions.Online, address);
                }
            }

            @Override
            public void sendMsgCallback(String address, String msg) {
                UserBean remoteUser = findUserInTable(address);
                if (remoteUser != null) {
                    //添加消息记录
                    addRecord(false, remoteUser, msg);
                }
                //消息闪烁
                if (userList.getSelectedIndex() != allUsers.indexOf(remoteUser)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (int i = 0; i < 3; i++) {
                                    allUsers.get(allUsers.indexOf(remoteUser)).setRecMsg(true);
                                    Thread.sleep(300);
                                    userList.repaint();
                                    allUsers.get(allUsers.indexOf(remoteUser)).setRecMsg(false);
                                    Thread.sleep(300);
                                    userList.repaint();
                                }
                                allUsers.get(allUsers.indexOf(remoteUser)).setRecMsg(true);
                                Thread.sleep(300);
                                userList.repaint();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

                //使用这个方法将UI放到AWT-EventQueue-0线程执行
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        userList.repaint();
                        recordList.setListData(currentRecords);
                        recordList.updateUI();
                        recordList.repaint();
                    }
                });
            }

            @Override
            public void leaveCallback(String username, String hostName, String address) {
                removeOfflineUser(address);
            }
        });
//        UserBean user1 = new UserBean("11", "22", "33", Constant.iconURL[1]);
//        UserBean user2 = new UserBean("66", "55", "44", Constant.iconURL[19]);
//        addOnlineUser(user1);
//        addOnlineUser(user2);
//        addRecord(true, user1, "@222");
//        addRecord(false, user1, "@3333");
//        addRecord(true, user2, "@444");
//        addRecord(false, user2, "@555");

    }

    private void initLocalUser() {
        try {
            InetAddress local = InetAddress.getLocalHost();
            //注：在其他主机上getHostName是获取主机名，在本机上getHostName是获取当前用户名
//            localUser = new UserBean(System.getProperty("user.name"), local.getHostName(), local.getHostAddress());
            localUser = findUserInTable(local.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, Constant.HINT_BIND_LOCAL_ERROR);
        }
    }

    private void initStartPos() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenDimen = tk.getScreenSize();
        Dimension frameDimen = mainFrame.getContentPane().getSize();
        mainFrame.setLocation((screenDimen.width - frameDimen.width) / 2, (screenDimen.height - frameDimen.height) / 2);
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        MyMenuItemListener myMenuItemListener = new MyMenuItemListener();
        for (int i = 0; i < Constant.menus.length; i++) {
            menus.put(Constant.menus[i], Constant.menuItems[i]);
            JMenu menu = new JMenu(Constant.menus[i]);
            for (int j = 0; j < Constant.menuItems[i].length; j++) {
                JMenuItem menuItem = new JMenuItem(Constant.menuItems[i][j]);
                menuItem.setPreferredSize(new Dimension(150, 50));
                ImageIcon imageIcon = new ImageIcon(Main.class.getResource(Constant.menuItemsIconURL[i][j]));
                imageIcon.setImage(imageIcon.getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT));
                menuItem.setIcon(imageIcon);
                menu.add(menuItem);
                menuItem.addActionListener(myMenuItemListener);
            }
            menuBar.add(menu);
        }
        mainFrame.setJMenuBar(menuBar);
    }

    private void initSplitPanel() {
        JSplitPane hSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JSplitPane vSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        listPanel = new JPanel();
        msgPanel = new JPanel();
        chatPanel = new JPanel();
        msgPanel.setLayout(new GridLayout(1, 1));

        mainFrame.add(hSplitPane);

        hSplitPane.setDividerLocation(250);
        hSplitPane.setDividerSize(5);
        hSplitPane.setLeftComponent(listPanel);
        hSplitPane.setRightComponent(vSplitPane);

        vSplitPane.setDividerLocation(Constant.defaultWndHeight / 5 * 3);
        vSplitPane.setDividerSize(5);
        vSplitPane.setTopComponent(msgPanel);
        vSplitPane.setBottomComponent(chatPanel);
    }

    private void initChatPanel() {

        inputArea = new JTextArea();
        inputArea.setLineWrap(true);
        inputArea.setFont(new Font(null, Font.PLAIN, 18));
        JScrollPane inputPanel = new JScrollPane(inputArea);
        inputPanel.setBorder(new LineBorder(null, 0));

        JButton btnFile = new JButton("文件");
        JButton btnSend = new JButton("发送");

        FileChooserBtnListener fileChooserBtnListener = new FileChooserBtnListener();
//        btnFile.registerKeyboardAction(fileChooserBtnListener, KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        btnFile.addActionListener(fileChooserBtnListener);

//        btnSend.setPreferredSize(new Dimension(60, 24));

        SendMsgBtnListener sendMsgBtnListener = new SendMsgBtnListener();
        btnSend.addActionListener(sendMsgBtnListener);
        inputArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "send");
        inputArea.getActionMap().put("send", sendMsgBtnListener);

        JPanel sendPanel = new JPanel();
        sendPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        sendPanel.setBackground(Color.white);
        sendPanel.add(btnFile);
        sendPanel.add(btnSend);

        chatPanel.setLayout(new BorderLayout());
        chatPanel.add(inputPanel, BorderLayout.CENTER);
        chatPanel.add(sendPanel, BorderLayout.SOUTH);
    }

    private void initUserListPanel() {
        userList = new JList();
        userList.setCellRenderer(new UserInfoListRenderer());
        userList.setModel(userInfoListModel);
        userList.setFont(new Font(null, Font.PLAIN, 14));
        listPanel.setLayout(new BorderLayout());
        listPanel.add(userList, BorderLayout.NORTH);
        onlineNum = new JLabel("在线人数：0");
        listPanel.add(onlineNum, BorderLayout.SOUTH);
        listPanel.setBackground(Constant.themeColor01);
        userList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                changeMsgPanel(userList.getSelectedIndex());
                remoteUser = allUsers.get(userList.getSelectedIndex());
                allUsers.get(userList.getSelectedIndex()).setRecMsg(false);
            }
        });
    }


    private void initMsgPanel() {
        recordList = new JList();
        recordList.setCellRenderer(new RecordListRenderer());
        recordList.setModel(recordListModel);
        JScrollPane recordPanel = new JScrollPane(recordList);
        msgPanel.add(recordPanel);
    }

    private void changeMsgPanel(int index) {
        Vector<RecordItemEntity> records = recordSave.getRecords(allUsers.get(index).getAddress());
        currentRecords = records;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                recordList.setListData(currentRecords);
                recordList.updateUI();
                recordList.repaint();
            }
        });
    }

    private void initUI() {
        initSplitPanel();
        initChatPanel();
        initMsgPanel();
        initUserListPanel();
    }

    private void initUserInfoDialog() {
        userInfoTable = new JTable();
        userInfoTable.setForeground(Color.BLACK);
        userInfoTable.setRowHeight(30);
        userInfoTable.setFont(new Font(null, Font.PLAIN, 14));
        userInfoTable.setSelectionBackground(Color.LIGHT_GRAY);
        userInfoTable.setGridColor(Color.GRAY);
        userInfoTable.getTableHeader().setFont(new Font(null, Font.BOLD, 14));
        userInfoTable.getTableHeader().setForeground(Color.BLACK);

        userInfoTable.setModel(userInfoTableModel);

        JScrollPane userInfoPanel = new JScrollPane(userInfoTable);

        JPanel bottomPanel = new JPanel();
        JButton updateBtn = new JButton("刷新");
        updateBtn.setFont(new Font(null, Font.PLAIN, 14));
        updateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateOnlineUsers();
            }
        });
        bottomPanel.add(updateBtn);
        userInfoDialog = new JDialog(mainFrame, "用户信息");
        userInfoDialog.setLayout(new BorderLayout());
        userInfoDialog.add(userInfoPanel);
        userInfoDialog.add(bottomPanel, BorderLayout.SOUTH);
        userInfoDialog.pack();
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenDimen = tk.getScreenSize();
        Dimension dialogDimen = userInfoDialog.getContentPane().getSize();
        userInfoDialog.setLocation((screenDimen.width - dialogDimen.width) / 2, (screenDimen.height - dialogDimen.height) / 2);
    }

    private void showUserInfoDialog() {
        userInfoDialog.setVisible(true);
    }

    class MyMenuItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (((JMenuItem) e.getSource()).getText()) {
                case "用户信息":
                    showUserInfoDialog();
                    break;
                case "传输":
                    TransferDialog.getInstance().showTransferDialog();
                    break;
                case "帮助":
                    JOptionPane.showMessageDialog(null, Constant.help, "帮助", JOptionPane.INFORMATION_MESSAGE);
                    break;
            }
        }
    }

    class FileChooserBtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            showFileChooserDialog();
        }
    }

    private void showFileChooserDialog() {
        JFileChooser jFileChooser = new JFileChooser();
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        jFileChooser.setCurrentDirectory(fileSystemView.getHomeDirectory());
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int result = jFileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile();
            if (file != null) {
                int option = JOptionPane.showConfirmDialog(null,
                        "文件名称：" + file.getName() + "\n" + "文件大小：" + FileUtil.getFormatFileSize(FileUtil.getTotalSize(file)) + "\n" +
                                "-------------是否发送？-------------",
                        "文件信息",
                        JOptionPane.YES_NO_OPTION);
                if (option == 0) {
                    if (remoteUser == null) {
                        JOptionPane.showMessageDialog(null, Constant.HINT_NULL_REMOTE_USER);
                    } else {
                        TransferBean transferBean = new TransferBean(file.getName(), FileUtil.getTotalSize(file));
                        TransferDialog.getInstance().addSendElement(transferBean);
                        TransferDialog.getInstance().sendUpdateUI();
                        TransferDialog.getInstance().sendRepaint();
                        TCPClient tcpClientRunnable = new TCPClient(localUser, remoteUser.getAddress(), file, transferBean);
                        Thread clientThread = new Thread(tcpClientRunnable);
                        clientThread.start();
                    }
                }
            }
        }
    }

    class SendMsgBtnListener extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            //向指定用户发送消息
            InetAddress address;
            if (remoteUser == null) {
                JOptionPane.showMessageDialog(null, Constant.HINT_NULL_REMOTE_USER);
            } else {
                if (inputArea.getText() == null || inputArea.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, Constant.HINT_NULL_MSG);
                } else {
                    addRecord(true, remoteUser, inputArea.getText());
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            recordList.updateUI();
                            recordList.repaint();
                        }
                    });
                    try {
                        address = InetAddress.getByName(remoteUser.getAddress());
                        Thread sendMsg = new Thread(new UDPSend(NetOptions.Msg, address, inputArea.getText()));
                        sendMsg.start();
                        inputArea.setText("");
                    } catch (UnknownHostException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    private void addRecord(boolean isMine, UserBean user, String msg) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time = format.format(date);
        RecordItemEntity recordItemEntity;
        if (isMine)
            recordItemEntity = new RecordItemEntity(localUser.getUsername(), time, msg, localUser.getIconUrl(), isMine);
        else
            recordItemEntity = new RecordItemEntity(user.getUsername(), time, msg, user.getIconUrl(), isMine);
        recordSave.addRecord(user.getAddress(), recordItemEntity);
    }

    //检查是否已经存在相同ip的用户
    private boolean checkUserInTable(String ip) {
        for (int i = 0; i < userInfoTableModel.getRowCount(); i++) {
            if (ip.equals((String) userInfoTableModel.getValueAt(i, 2))) return true;
        }
        return false;
    }

    private UserBean findUserInTable(String ip) {
        for (int i = 0; i < userInfoTableModel.getRowCount(); i++) {
            if (ip.equals((String) userInfoTableModel.getValueAt(i, 2)))
                return allUsers.get(i);
        }
        return null;
    }

    private void updateOnlineUsers() {
        onlineNum.setText("在线人数：" + allUsers.size());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                onlineNum.repaint();
                userList.updateUI();
                userInfoTable.repaint();
            }
        });
    }

    public void addOnlineUser(UserBean userBean) {
        if (checkUserInTable(userBean.getAddress())) {
            return;
        }
        try {
            allUsers.addElement(userBean);
            recordSave.addUser(userBean.getAddress());
            updateOnlineUsers();
        } catch (Exception e) {
        }
    }

    public void removeOfflineUser(String address) {
        allUsers.remove(findUserInTable(address));
        recordSave.removeUser(address);
        currentRecords = recordSave.getRecords(allUsers.get(0).getAddress());
        recordList.setListData(currentRecords);
        userList.setSelectedIndex(0);
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                recordList.repaint();
            }
        });
        updateOnlineUsers();
    }
}
