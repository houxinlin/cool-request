package com.hxl.plugin.springboot.invoke.view.dialog;

import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class BugDialog extends DialogWrapper {
    private JPanel contentPane;
    private JLabel tip;
    private JTextArea text;
    private JLabel commitTip;

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }

    public BugDialog(Project project) {
        super(project);
        setSize(650, 300);
        init();
        tip.setText(ResourceBundleUtils.getString("bug.tip"));
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setBorder(BorderFactory.createLineBorder(Color.decode("#787878")));
    }

    @Override
    protected void doOKAction() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder()
                .post(RequestBody.create(text.getText(), MediaType.parse("text/paint")))
                .url("http://plugin.houxinlin.com/api/bug");
        okHttpClient.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                SwingUtilities.invokeLater(() -> commitTip.setText((ResourceBundleUtils.getString("commit.fail"))));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                onCommitResponse(response);
            }
        });
    }

    private void onCommitResponse(Response response) {
        SwingUtilities.invokeLater(() -> {
            if (response.code() == 200) {
                BugDialog.super.doOKAction();
                Messages.showInfoMessage(ResourceBundleUtils.getString("commit.ok"), ResourceBundleUtils.getString("tip"));
                return;
            }
            commitTip.setText((ResourceBundleUtils.getString("commit.fail")));
        });
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }

}
