package com.hxl.plugin.springboot.invoke.view;

import com.hxl.plugin.springboot.invoke.bean.RequestMappingInvokeBean;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ControllerInvokeDialog extends DialogWrapper {
    private JPanel contentPane;
    private JTextField textField1;
    private JRadioButton radioButton1;
    private JRadioButton radioButton4;

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }
    private RequestMappingInvokeBean requestMappingInvokeBean;
    private int port;
    private InvokeDialog.Callback callback;
    public ControllerInvokeDialog(RequestMappingInvokeBean requestMappingInvokeBean, int port, InvokeDialog.Callback callback) {
        super(false);
        this.requestMappingInvokeBean = requestMappingInvokeBean;
        this.port = port;
        this.callback = callback;
        setTitle("调用");
        init();

    }


//    public ControllerInvokeDialog() {
////        setContentPane(contentPane);
//        setModal(true);
//        getRootPane().setDefaultButton(buttonOK);
//
//        buttonOK.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onOK();
//            }
//        });
//
//        buttonCancel.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onCancel();
//            }
//        });
//
//        // call onCancel() when cross is clicked
////        addWindowListener(new WindowAdapter() {
////            public void windowClosing(WindowEvent e) {
////                onCancel();
////            }
////        });
//
//        // call onCancel() on ESCAPE
//        contentPane.registerKeyboardAction(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onCancel();
//            }
//        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

//    public static void main(String[] args) {
//        ControllerInvokeDialog dialog = new ControllerInvokeDialog();
//        dialog.pack();
//        dialog.setVisible(true);
//        System.exit(0);
//    }
}
