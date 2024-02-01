package com.cool.request.view.widget;

import java.awt.*;


public class PanelAction extends javax.swing.JPanel {
    private ActionButton[] actionButton;

    public PanelAction(ActionButton... actionButton) {
        initComponents(actionButton);
    }

    private void initComponents(ActionButton... actionButton) {
        this.setLayout(new FlowLayout());
        this.actionButton = actionButton;
        for (ActionButton button : actionButton) {
            add(button);
        }
    }

    public void setRow(int row) {

    }
}
