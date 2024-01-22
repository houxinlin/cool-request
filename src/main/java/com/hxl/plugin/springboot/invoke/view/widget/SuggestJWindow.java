package com.hxl.plugin.springboot.invoke.view.widget;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import org.jdesktop.swingx.border.DropShadowBorder;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SuggestJWindow extends JWindow {
    private static final int MAX_HEIGHT = 150;
    private List<String> suggest;
    private JBTextField jbTextField;
    private Project project;
    private JBList<String> suggestList = new JBList<>();
    private DefaultListModel<String> defaultListModel = new DefaultListModel<>();
    private ItemSelect itemSelect;

    public void setItemSelect(ItemSelect itemSelect) {
        this.itemSelect = itemSelect;
    }

    /**
     * 将文本框和window关联
     * @param jbTextField
     * @param suggest
     * @param project
     * @return
     */
    public static SuggestJWindow attachJTextField(JBTextField jbTextField,
                                                  List<String> suggest,
                                                  Project project) {
        return new SuggestJWindow(jbTextField, suggest, project);

    }

    public void setSuggest(List<String> suggest) {
        this.suggest = suggest;
    }

    private SuggestJWindow(JBTextField jbTextField,
                           List<String> suggest,
                           Project project) {
        this.jbTextField = jbTextField;
        this.project = project;
        this.suggest = suggest;
        this.suggestList.setModel(defaultListModel);
        getContentPane().add(new JBScrollPane(this.suggestList));
        DropShadowBorder shadowBorder = new DropShadowBorder();
        this.suggestList.setBorder(shadowBorder);
        project.getMessageBus().connect().subscribe(IdeaTopic.IDEA_FRAME_EVENT_TOPIC, new IdeaTopic.IdeaFrameEvent() {
            @Override
            public void windowsMovedEvent(ComponentEvent event) {
                IdeaTopic.IdeaFrameEvent.super.windowsMovedEvent(event);
                calculateWindowPoint();
            }
        });
        suggestList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    int index = suggestList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        String selectedItem = defaultListModel.getElementAt(index);
                        jbTextField.setText(selectedItem);
                        setVisible(false);
                    }
                }
            }
        });
        jbTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                calculateWindowPoint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                setVisible(false);
            }
        });
        jbTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                showSuggest();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                showSuggest();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                showSuggest();
            }
        });

    }

    private void calculateWindowPoint() {
        Rectangle bounds = jbTextField.getBounds();
        Point locationOnScreen = jbTextField.getLocationOnScreen();
        setSize(new Dimension(((int) bounds.getWidth()), MAX_HEIGHT));
        setLocation(new Point(((int) locationOnScreen.getX()), jbTextField.getHeight() + ((int) locationOnScreen.getY())));
        suggestList.setSize(getSize());
    }

    private boolean applySuggest() {
        if (suggest == null || suggest.size() == 0) return false;
        if (jbTextField.getText().length() == 0) return false;
        defaultListModel.clear();
        for (String item : suggest) {
            if (item.toLowerCase().contains(jbTextField.getText().toLowerCase())) {
                defaultListModel.addElement(item);
            }
        }
        return defaultListModel.getSize() > 0;
    }

    private void showSuggest() {
        if (this.suggest == null || this.suggest.isEmpty()) return;
        setVisible(applySuggest());
        if (isVisible()) calculateWindowPoint();
    }

    public interface ItemSelect {
        public void onSelect(String value);
    }
}
