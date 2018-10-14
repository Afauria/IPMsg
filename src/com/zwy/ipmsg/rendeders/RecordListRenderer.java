package com.zwy.ipmsg.rendeders;

import com.zwy.ipmsg.beans.RecordItemEntity;
import sun.security.tools.keytool.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Created by 赤脚和尚 on 2018/3/28.
 */
public class RecordListRenderer implements ListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        RecordItemEntity recordItemEntity = (RecordItemEntity) value;
        JPanel panel = new JPanel();
        JPanel leftPanel=new JPanel();
        JPanel rightPanel=new JPanel();
        JPanel topPanel = new JPanel();
        JPanel bottomPanel = new JPanel();
        RoundPanel contentBG = new RoundPanel();
        //  panel.setBorder(BorderFactory.createEtchedBorder());
        leftPanel.setBackground(Color.white);
        rightPanel.setBackground(Color.white);
        topPanel.setBackground(Color.white);
        bottomPanel.setBackground(Color.white);
        panel.setBackground(Color.white);

        String str = recordItemEntity.getContent();
        StringBuilder contentStr = new StringBuilder();
        contentStr.append("<html>");
        contentStr.append(str);
        for (int i = 1; i <= str.length() / 40; i++) {
            contentStr.insert(40 * i + 1, "<br/>");
            str += "<br/>";
        }
        contentStr.append("</html>");
        JLabel name = new JLabel(recordItemEntity.getName());
        JLabel time = new JLabel(recordItemEntity.getTime());
        JLabel content = new JLabel(contentStr.toString());
        name.setForeground(new Color(100, 100, 100));
        time.setForeground(new Color(100, 100, 100));
        name.setFont(new Font(null, Font.BOLD, 14));
        time.setFont(new Font(null, Font.BOLD, 14));
        content.setForeground(Color.BLACK);
        contentBG.setBackground(new Color(228, 228, 228));
        contentBG.setPreferredSize(new Dimension(content.getPreferredSize().width + 20, content.getPreferredSize().height + 15));
        contentBG.add(content);

        topPanel.setLayout(new FlowLayout(recordItemEntity.isMine() ? FlowLayout.RIGHT : FlowLayout.LEFT, 20, 3));
        bottomPanel.setLayout(new FlowLayout(recordItemEntity.isMine() ? FlowLayout.RIGHT : FlowLayout.LEFT, 10, 5));
        topPanel.add(name);
        topPanel.add(time);
        bottomPanel.add(contentBG);

        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(topPanel);
        rightPanel.add(bottomPanel);

        leftPanel.setPreferredSize(new Dimension(80,80));
        JLabel img=new JLabel();
        ImageIcon icon=new ImageIcon(Main.class.getResource(recordItemEntity.getIconUrl()));
        int width=70, height=70;
        icon.setImage(icon.getImage().getScaledInstance(width,height,Image.SCALE_DEFAULT));
        img.setIcon(icon);
        leftPanel.add(img);

        panel.setLayout(new FlowLayout(recordItemEntity.isMine()?FlowLayout.RIGHT:FlowLayout.LEFT));
        if(recordItemEntity.isMine()){
            panel.add(rightPanel);
            panel.add(leftPanel);
        }else{
            panel.add(leftPanel);
            panel.add(rightPanel);
        }
        return panel;
    }

    class RoundPanel extends JPanel {
        @Override
        public void paint(Graphics g) {
            g.setClip(new RoundRectangle2D.Double(0, 0, getPreferredSize().width, getPreferredSize().height, 8, 8));
            super.paint(g);
        }
    }
}
