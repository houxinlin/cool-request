package com.cool.request.view.widget;

import com.cool.request.IdeaTopic;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.function.Supplier;


public class AutoSuggestor {
    private JBList<Item> suggestList = new JBList<>();
    private DefaultListModel<Item> defaultListModel = new DefaultListModel<>();
    private final JTextField textField;
    private final Window container;
    private JPanel suggestionsPanel;
    private JWindow autoSuggestionPopUpWindow;
    private List<Item> suggest;
    private DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent de) {
            checkForAndShowSuggestions();
        }

        @Override
        public void removeUpdate(DocumentEvent de) {
            checkForAndShowSuggestions();
        }

        @Override
        public void changedUpdate(DocumentEvent de) {
            checkForAndShowSuggestions();
        }
    };

    public static AutoSuggestor attachJTextField(JTextField textField, Window mainWindow, List<Item> suggest, Project project) {
        return new AutoSuggestor(textField, mainWindow, suggest, project);
    }

    public AutoSuggestor(JTextField textField, Window mainWindow, List<Item> words, Project project) {
        this.textField = textField;
        this.container = mainWindow;
        this.textField.getDocument().addDocumentListener(documentListener);
        this.suggest = words;

        autoSuggestionPopUpWindow = new JWindow(mainWindow);
        suggestionsPanel = new JPanel(new BorderLayout());
        suggestionsPanel.setLayout(new GridLayout(0, 1));
        autoSuggestionPopUpWindow.getContentPane().add(new JBScrollPane(suggestList));
        this.suggestList.setModel(defaultListModel);
        autoSuggestionPopUpWindow.setType(Window.Type.POPUP);
        suggestList.setCellRenderer(new SuggestDefaultListCellRenderer());
        if (mainWindow != null) {
            mainWindow.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentMoved(ComponentEvent e) {
                    super.componentMoved(e);
                    if (autoSuggestionPopUpWindow.isShowing()) {
                        showPopUpWindow();
                    }
                }
            });
        }
        if (mainWindow == null) {
            project.getMessageBus().connect().subscribe(IdeaTopic.IDEA_FRAME_EVENT_TOPIC, new IdeaTopic.IdeaFrameEvent() {
                @Override
                public void windowsMovedEvent(ComponentEvent event) {
                    IdeaTopic.IdeaFrameEvent.super.windowsMovedEvent(event);
                    if (autoSuggestionPopUpWindow.isShowing()) {
                        showPopUpWindow();
                    }
                }

                @Override
                public void windowLostFocus(WindowEvent e) {
                    IdeaTopic.IdeaFrameEvent.super.windowLostFocus(e);
                    autoSuggestionPopUpWindow.setVisible(false);
                }
            });
        }
        suggestList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    textField.getDocument().removeDocumentListener(documentListener);
                    int index = suggestList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        Item item = defaultListModel.getElementAt(index);
                        textField.setText(item.convertValue());
                        autoSuggestionPopUpWindow.setVisible(false);
                    }
                    textField.getDocument().addDocumentListener(documentListener);
                }
            }
        });
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                autoSuggestionPopUpWindow.setVisible(false);
            }
        });

    }

    private void setFocusToTextField() {
        if (container != null) {
            container.toFront();
            container.requestFocusInWindow();
        }
        textField.requestFocusInWindow();
    }

    private void checkForAndShowSuggestions() {
        boolean added = wordTyped();
        if (!added) {
            if (autoSuggestionPopUpWindow.isVisible()) {
                autoSuggestionPopUpWindow.setVisible(false);
            }
        } else {
            showPopUpWindow();
            setFocusToTextField();
        }
    }

    private void showPopUpWindow() {
        autoSuggestionPopUpWindow.setMinimumSize(new Dimension(textField.getWidth(), 150));
        autoSuggestionPopUpWindow.setVisible(true);
        Point locationOnScreen = textField.getLocationOnScreen();
        Rectangle bounds = textField.getBounds();
        autoSuggestionPopUpWindow.setSize(new Dimension(((int) bounds.getWidth()), 10));
        autoSuggestionPopUpWindow.setLocation(new Point(((int) locationOnScreen.getX()), textField.getHeight() + ((int) locationOnScreen.getY())));

        autoSuggestionPopUpWindow.revalidate();
        autoSuggestionPopUpWindow.repaint();

    }

    public JWindow getAutoSuggestionPopUpWindow() {
        return autoSuggestionPopUpWindow;
    }

    public Window getContainer() {
        return container;
    }

    public JTextField getTextField() {
        return textField;
    }

    boolean wordTyped() {
        if (suggest == null || suggest.size() == 0) return false;
        if (textField.getText().length() == 0) return false;
        defaultListModel.clear();
        for (Item item : suggest) {
            if (item.getDisplay().toLowerCase().contains(textField.getText().toLowerCase())) {
                defaultListModel.addElement(item);
            }
        }
        return defaultListModel.getSize() > 0;
    }

    public void setSuggest(List<Item> items) {
        this.suggest = items;
    }

    public static class SuggestDefaultListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            String rendererValue = "";
            if (value instanceof Item) {
                rendererValue = ((Item) value).getDisplay();
            }
            return super.getListCellRendererComponent(list, rendererValue, index, isSelected, cellHasFocus);
        }
    }

    public interface ItemSelect {
        public void onSelect(String value);
    }

    public static abstract class Item {
        public abstract String getDisplay();

        public abstract String convertValue();
    }

    public static class FunctionItem extends Item {
        private String name;
        private Supplier<String> valueSupplier;

        public FunctionItem(String name, Supplier<String> valueSupplier) {
            this.name = name;
            this.valueSupplier = valueSupplier;
        }

        @Override
        public String getDisplay() {
            return name;
        }

        @Override
        public String convertValue() {
            return valueSupplier.get();
        }
    }

    public static class SimpleStringItem extends Item {
        private String name;

        public SimpleStringItem(String name) {
            this.name = name;
        }

        @Override
        public String getDisplay() {
            return name;
        }

        @Override
        public String convertValue() {
            return name;
        }
    }
}
