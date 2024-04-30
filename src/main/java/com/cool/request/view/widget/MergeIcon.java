/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * MergeIcon.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.view.widget;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.icons.CompositeIcon;
import com.intellij.ui.icons.DarkIconProvider;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IconUtil;
import com.intellij.util.ui.JBCachingScalableIcon;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static com.intellij.ui.scale.ScaleType.OBJ_SCALE;
import static com.intellij.ui.scale.ScaleType.USR_SCALE;

public class MergeIcon extends JBCachingScalableIcon<MergeIcon> implements DarkIconProvider, CompositeIcon {
    private static final Logger LOG = Logger.getInstance(MergeIcon.class);
    private final Icon[] myIcons;
    private Icon[] myScaledIcons;
    private final boolean[] myDisabledLayers;
    private final int[] myHShifts;
    private final int[] myVShifts;

    private int myXShift;
    private int myYShift;

    private int myWidth;
    private int myHeight;

    {
        getScaleContext().addUpdateListener(this::updateSize);
        setAutoUpdateScaleContext(false);
    }

    public MergeIcon(int layerCount) {
        myIcons = new Icon[layerCount];
        myDisabledLayers = new boolean[layerCount];
        myHShifts = new int[layerCount];
        myVShifts = new int[layerCount];
    }

    public MergeIcon(Icon @NotNull ... icons) {
        this(icons.length);
        int x = 0;
        int y = 0;
        for (int i = 0; i < icons.length; i++) {
            setIcon(icons[i], i, x, 0);
            x += icons[i].getIconWidth();
        }
    }

    protected MergeIcon(@NotNull MergeIcon icon) {
        super(icon);
        myIcons = ArrayUtil.copyOf(icon.myIcons);
        myScaledIcons = null;
        myDisabledLayers = ArrayUtil.copyOf(icon.myDisabledLayers);
        myHShifts = ArrayUtil.copyOf(icon.myHShifts);
        myVShifts = ArrayUtil.copyOf(icon.myVShifts);
        myXShift = icon.myXShift;
        myYShift = icon.myYShift;
        myWidth = icon.myWidth;
        myHeight = icon.myHeight;
    }

    @NotNull
    @Override
    public MergeIcon copy() {
        return new MergeIcon(this);
    }

    @NotNull
    @Override
    public MergeIcon deepCopy() {
        MergeIcon icon = new MergeIcon(this);
        for (int i = 0; i < icon.myIcons.length; i++) {
            icon.myIcons[i] = icon.myIcons[i] == null ? null : IconUtil.copy(icon.myIcons[i], null);
        }
        return icon;
    }

    private Icon @NotNull [] myScaledIcons() {
        if (myScaledIcons != null) {
            return myScaledIcons;
        }
        return myScaledIcons = scaleIcons(myIcons, getScale());
    }

    static Icon @NotNull [] scaleIcons(Icon @NotNull [] icons, float scale) {
        if (scale == 1f) return icons;
        Icon[] scaledIcons = new Icon[icons.length];
        for (int i = 0; i < icons.length; i++) {
            if (icons[i] != null) {
                scaledIcons[i] = IconUtil.scale(icons[i], null, scale);
            }
        }
        return scaledIcons;
    }

    @NotNull
    @Override
    public MergeIcon withIconPreScaled(boolean preScaled) {
        super.withIconPreScaled(preScaled);
        updateSize();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MergeIcon)) return false;

        final MergeIcon icon = (MergeIcon) o;

        if (myHeight != icon.myHeight) return false;
        if (myWidth != icon.myWidth) return false;
        if (myXShift != icon.myXShift) return false;
        if (myYShift != icon.myYShift) return false;
        if (!Arrays.equals(myHShifts, icon.myHShifts)) return false;
        if (!Arrays.equals(myIcons, icon.myIcons)) return false;
        if (!Arrays.equals(myVShifts, icon.myVShifts)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public void setIcon(Icon icon, int layer) {
        setIcon(icon, layer, 0, 0);
    }

    @Override
    public Icon getIcon(int layer) {
        return myIcons[layer];
    }

    @Override
    public int getIconCount() {
        return myIcons.length;
    }

    public void setIcon(Icon icon, int layer, int hShift, int vShift) {
        if (icon instanceof MergeIcon) {
            ((MergeIcon) icon).checkIHaventIconInsideMe(this);
        }
        myIcons[layer] = icon;
        myScaledIcons = null;
        myHShifts[layer] = hShift;
        myVShifts[layer] = vShift;
        updateSize();
    }

    private void checkIHaventIconInsideMe(Icon icon) {
        LOG.assertTrue(icon != this);
        for (Icon child : myIcons) {
            if (child instanceof MergeIcon) ((MergeIcon) child).checkIHaventIconInsideMe(icon);
        }
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        getScaleContext().update();
        Icon[] icons = myScaledIcons();
        for (int i = 0; i < icons.length; i++) {
            Icon icon = icons[i];
            if (icon == null || myDisabledLayers[i]) continue;
            int xOffset = (int) Math.floor(x + scaleVal(myXShift + myHShifts(i), OBJ_SCALE));
            int yOffset = (int) Math.floor(y + scaleVal(myYShift + myVShifts(i), OBJ_SCALE));
            icon.paintIcon(c, g, xOffset, yOffset);
        }
    }

    @Override
    public int getIconWidth() {
        getScaleContext().update();
        if (myWidth <= 1) updateSize();

        return (int) Math.ceil(scaleVal(myWidth, OBJ_SCALE));
    }

    @Override
    public int getIconHeight() {
        getScaleContext().update();
        if (myHeight <= 1) updateSize();

        return (int) Math.ceil(scaleVal(myHeight, OBJ_SCALE));
    }

    private int myHShifts(int i) {
        return (int) Math.floor(scaleVal(myHShifts[i], USR_SCALE));
    }

    private int myVShifts(int i) {
        return (int) Math.floor(scaleVal(myVShifts[i], USR_SCALE));
    }

    protected void updateSize() {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        boolean allIconsAreNull = true;
        for (int i = 0; i < myIcons.length; i++) {
            Icon icon = myIcons[i];
            if (icon == null) continue;
            allIconsAreNull = false;
            int hShift = myHShifts(i);
            int vShift = myVShifts(i);
            minX = Math.min(minX, hShift);
            maxX = Math.max(maxX, hShift + icon.getIconWidth());
            minY = Math.min(minY, vShift);
            maxY = Math.max(maxY, vShift + icon.getIconHeight());
        }
        if (allIconsAreNull) return;
        myWidth = maxX - minX;
        myHeight = maxY - minY;

        if (myIcons.length > 1) {
            myXShift = -minX;
            myYShift = -minY;
        }
    }

    @NotNull
    @Override
    public Icon getDarkIcon(boolean isDark) {
        MergeIcon newIcon = copy();
        for (int i = 0; i < newIcon.myIcons.length; i++) {
            newIcon.myIcons[i] = newIcon.myIcons[i] == null ? null : IconLoader.getDarkIcon(newIcon.myIcons[i], isDark);
        }
        return newIcon;
    }
}
