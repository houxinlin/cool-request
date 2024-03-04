package com.cool.request.view;

import com.intellij.util.ui.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MergedIcon implements Icon {
    private final BufferedImage imageBuffer;
    private int sizeCount = 0;

    public MergedIcon(List<Icon> icons) {
        List<Image> images = icons.stream().map(MergedIcon::iconToImage).collect(Collectors.toList());
        sizeCount = images.size();
        imageBuffer = ImageUtil.createImage(16 * images.size(), 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) imageBuffer.getGraphics();

        int offsetX = 0;
        for (Image image : images) {
            g.drawImage(image, offsetX, 0, 16, 16, null);
            offsetX += 16;
        }
    }

    public MergedIcon(Icon... otherImage) {
        List<Image> images = Arrays.asList(otherImage).stream().map(MergedIcon::iconToImage).collect(Collectors.toList());
        sizeCount = images.size();
        imageBuffer = ImageUtil.createImage(16 * images.size(), 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) imageBuffer.getGraphics();

        int offsetX = 0;
        for (Image image : images) {
            g.drawImage(image, offsetX, 0, 16, 16, null);
            offsetX += 16;
        }
    }

    @Override
    public int getIconHeight() {
        return 16;
    }

    @Override
    public int getIconWidth() {
        return sizeCount * 16;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.drawImage(imageBuffer, x, y, null);
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