package com.zwy.ipmsg.utils;

import javax.swing.*;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

public class ImageUtil {
    public static BufferedImage getBufferedImage(ImageIcon icon) {
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();
        ImageObserver observer = icon.getImageObserver();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics gc = bufferedImage.createGraphics();
        ((Graphics2D) gc).drawImage(icon.getImage(), 0, 0, observer);
        return bufferedImage;
    }

    public static BufferedImage getBufferedImage(Image icon) {
        int width = icon.getWidth(null);
        int height = icon.getHeight(null);
        BufferedImage bufferedImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_BGR);
        Graphics gc = bufferedImage.createGraphics();
        ((Graphics2D) gc).drawImage(icon, 0, 0, null);
        return bufferedImage;
    }

    public static BufferedImage setRadius(BufferedImage srcImage, int radius, int border, int padding) throws IOException {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        int canvasWidth = width + padding * 2;
        int canvasHeight = height + padding * 2;

        BufferedImage image = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gs = image.createGraphics();
        gs.setComposite(AlphaComposite.Src);
        gs.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gs.setColor(Color.WHITE);
        gs.fill(new RoundRectangle2D.Float(0, 0, canvasWidth, canvasHeight, radius, radius));
        gs.setComposite(AlphaComposite.SrcAtop);
        gs.drawImage(setClip(srcImage, radius), padding, padding, null);
        if (border != 0) {
            gs.setColor(Color.GRAY);
            gs.setStroke(new BasicStroke(border));
            gs.drawRoundRect(padding, padding, canvasWidth - 2 * padding, canvasHeight - 2 * padding, radius, radius);
        }
        gs.dispose();
        return image;
    }


    public static BufferedImage setRadius(BufferedImage srcImage) throws IOException {
        int radius = (srcImage.getWidth() + srcImage.getHeight()) / 6;
        return setRadius(srcImage, radius, 2, 5);
    }


    public static BufferedImage setClip(BufferedImage srcImage, int radius) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gs = image.createGraphics();

        gs.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gs.setClip(new RoundRectangle2D.Double(0, 0, width, height, radius, radius));
        gs.drawImage(srcImage, 0, 0, null);
        gs.dispose();
        return image;
    }
}
