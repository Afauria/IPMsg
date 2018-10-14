package com.zwy.ipmsg.rendeders;

import com.zwy.ipmsg.Constant;
import com.zwy.ipmsg.Main;
import com.zwy.ipmsg.beans.UserBean;

import javax.swing.*;
import java.awt.*;


public class UserInfoListRenderer implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        UserBean userBean = (UserBean) value;
        JPanel panel = new JPanel();
        JLabel img = new JLabel();
        JPanel panel2 = new JPanel();
        JLabel username = new JLabel();
        JLabel address = new JLabel();
        username.setText(userBean.getUsername());
        address.setText(userBean.getAddress());
        int width, height;
        height = 70;
        width = 70;
        ImageIcon icon = new ImageIcon(Main.class.getResource(userBean.getIconUrl()));
        icon.setImage(icon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
        img.setIcon(icon);

        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(img);
        panel.add(panel2);
        panel2.setLayout(new GridLayout(2, 1));
        panel2.setPreferredSize(new Dimension(list.getWidth() - 80, 70));
        panel2.setBackground(null);
        panel2.add(username);
        panel2.add(address);

        if (userBean.isRecMsg()) {
            panel.setBackground(Constant.themeColor03);
        } else {
            if (isSelected) {
                panel.setBackground(Constant.themeColor02);
            } else {
                panel.setBackground(Constant.themeColor01);
            }
        }
        return panel;
    }
}
