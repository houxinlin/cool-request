package com.hxl.plugin.springboot.invoke.view;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class MergedIcon implements Icon {

    private int m_iconWidth;
    private int m_iconHeight;
    private BufferedImage m_buffer;

    public MergedIcon(Icon backgroundImage, Icon topImage) {
        this(backgroundImage, topImage, 0, 0);
    }

    public MergedIcon(Icon backgroundImage, Icon topImage, int offsetX, int offsetY) {
        this(iconToImage(backgroundImage), iconToImage(topImage), offsetX, offsetY);
    }

    public MergedIcon(Image backgroundImage, Image topImage, int offsetX, int offsetY) {
        m_iconWidth = backgroundImage.getWidth(null);
        m_iconHeight = backgroundImage.getHeight(null);

        m_buffer = new BufferedImage(32, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) m_buffer.getGraphics();
        g.drawImage(backgroundImage, 0, 0, null);
        if (topImage != null) {
            g.drawImage(topImage, 16, offsetY, 16, 16, null);
        }
    }

    @Override
    public int getIconHeight() {
        return m_iconHeight;
    }

    @Override
    public int getIconWidth() {
        return 32;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.drawImage(m_buffer, x, y, null);
    }

    public static Image iconToImage(Icon icon) {
        if (icon == null)
            return null;
        if (icon instanceof ImageIcon)
            return ((ImageIcon) icon).getImage();

        return iconToBufferedImage(icon);
    }

    public static BufferedImage iconToBufferedImage(Icon icon) {
        if (icon == null)
            return null;

        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        icon.paintIcon(null, image.getGraphics(), 0, 0);
        return image;
    }
}