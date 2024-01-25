package com.cool.request.view.widget.btn.toggle;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.cool.request.view.widget.btn.Animator;
import com.cool.request.view.widget.btn.TimingTargetAdapter;
import com.cool.request.view.widget.btn.swing.shadow.ShadowRenderer;

public class ToggleButton extends JComponent {

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
            if (selected) {
                animate = 1f;
            } else {
                animate = 0;
            }
            repaint();
        }
    }

    public void setSelected(boolean selected, boolean animated) {
        if (this.selected != selected) {
            this.selected = selected;
            runEventSelected();
            if (animated) {
                start(selected);
            } else {
                if (selected) {
                    animate = 1f;
                } else {
                    animate = 0;
                }
                repaint();
            }
        }
    }

    public void addEventToggleSelected(ToggleListener event) {
        this.events.add(event);
    }

    private Animator animator;
    private float animate;
    private boolean selected;
    private boolean mousePress;
    private boolean mouseHover;
    private BufferedImage imageShadow;
    private final Insets shadowSize = new Insets(2, 5, 8, 5);
    private final List<ToggleListener> events = new ArrayList<>();

    public ToggleButton() {
        init();
        initAnimator();
    }

    private void initAnimator() {
        animator = new Animator(150, new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                if (isSelected()) {
                    animate = fraction;
                } else {
                    animate = 1f - fraction;
                }
                repaint();
                runEventAnimated();
            }
        });
        animator.setResolution(1);
    }

    private void init() {
        setPreferredSize(new Dimension(60, 35));
        setMaximumSize(new Dimension(60, 35));
        setSize(new Dimension(60, 35));
        setBackground(Color.decode("#07A889"));
        setForeground(Color.decode("#07A889"));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mouseHover = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseHover = false;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    mousePress = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (mousePress && mouseHover) {
                        setSelected(!isSelected(), true);
                    }
                    mousePress = false;
                }
            }
        });
    }

    private void start(boolean selected) {
        if (animator.isRunning()) {
            float f = animator.getTimingFraction();
            animator.stop();
            animator.setStartFraction(1f - f);
        } else {
            animator.setStartFraction(0);
        }
        this.selected = selected;
        animator.start();
    }

    private void runEventSelected() {
        for (ToggleListener event : events) {
            event.onSelected(selected);
        }
    }

    private void runEventAnimated() {
        for (ToggleListener event : events) {
            event.onAnimated(animate);
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (isOpaque()) {
            g2.setColor(getBackground());
            g2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
        }
        double width = getWidth() - (shadowSize.left + shadowSize.right);
        double height = getHeight() - (shadowSize.top + shadowSize.bottom);
        double h = height * 0.7;
        double x = shadowSize.left;
        double y = shadowSize.top + (height - h) / 2;
        //  Create background unselected color
        g2.setColor(Color.decode("#E74D3C"));
        g2.fill(new RoundRectangle2D.Double(x, y, width, h, h, h));
        //  Create background selected color
        //  Show background selected color 50% when selected
        g2.setColor(getForeground());
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, animate * 0.5f));
        g2.fill(new RoundRectangle2D.Double(x, y, width, h, h, h));
        //  Create image shadow
        //  And removed shadow image opacity 50% when selected 
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - animate * 0.5f));
        double location = shadowSize.left + (width - height) * animate;
        g2.drawImage(imageShadow, (int) location - shadowSize.left, 0, null);
        //  Create ellipse unselected color
        g2.setColor(Color.decode("#8C2E24"));
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - animate));
        Area area = new Area(new Ellipse2D.Double(location, shadowSize.top, height, height));
        g2.fill(area);
        //  Create ellipse selected color
        g2.setColor(getForeground());
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, animate));
        g2.fill(area);
        g2.dispose();
        super.paint(g);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        createImageShadow();
    }

    private void createImageShadow() {
        int height = getHeight();
        if (height <= 0) return;
        imageShadow = new BufferedImage(height, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imageShadow.createGraphics();
        g2.drawImage(createShadow(height), 0, 0, null);
        g2.dispose();
    }

    private BufferedImage createShadow(int size) {
        int width = size - (shadowSize.left + shadowSize.right);
        int height = size - (shadowSize.top + shadowSize.bottom);
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fill(new Ellipse2D.Double(0, 0, width, height));
        g2.dispose();
        return new ShadowRenderer(5, 0.5f, new Color(50, 50, 50)).createShadow(img);
    }
}
