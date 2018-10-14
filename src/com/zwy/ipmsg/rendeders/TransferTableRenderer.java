package com.zwy.ipmsg.rendeders;

import com.zwy.ipmsg.utils.FileUtil;
import sun.awt.windows.ThemeReader;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class TransferTableRenderer implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JPanel panel = new JPanel();
        if (column == 3) {
            if (Integer.parseInt(value.toString()) == -1) {
                panel.add(new JLabel("对方拒绝接收文件"));
            } else {
                JProgressBar progressBar = new JProgressBar(0, 100);
                progressBar.setValue(Integer.parseInt(value.toString()));
                panel.add(progressBar);
                progressBar.setPreferredSize(new Dimension(200, 30));
                progressBar.setDoubleBuffered(true);
                progressBar.setStringPainted(true);
            }
        } else if (column == 1 || column == 2) {
            long t = 0;
            try {
                t = Long.parseLong(value.toString());
            } catch (NumberFormatException e) {

            }
            panel.add(new JLabel(FileUtil.getFormatFileSize(t)));
        } else if (column == 4) {
            long t = 0;
            try {
                t = Long.parseLong(value.toString());
            } catch (NumberFormatException e) {

            }
            panel.add(new JLabel(FileUtil.getFormatFileSize(t) + "/s"));
        } else {
            panel.add(new JLabel(value.toString()));
        }

        return panel;
    }
}
