/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * BugDialog.java is part of Cool Request
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

package com.cool.request.view.dialog;

import com.cool.request.utils.ProgressWindowWrapper;
import com.cool.request.utils.ResourceBundleUtils;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class BugDialog extends DialogWrapper {
    private JPanel contentPane;
    private JLabel tip;
    private JTextArea text;
    private JLabel commitTip;
    private JLabel github;
    private JTextField email;
    private Project project;

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }

    public BugDialog(Project project) {
        super(project);
        this.project = project;
        setSize(650, 300);
        init();
        tip.setText(ResourceBundleUtils.getString("bug.tip"));
        github.setText(ResourceBundleUtils.getString("bug.tip.github"));
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setBorder(BorderFactory.createLineBorder(Color.decode("#787878")));
    }

    @Override
    protected void doOKAction() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build();
        Request.Builder builder = new Request.Builder()
                .post(RequestBody.create(text.getText() + email.getText(), MediaType.parse("text/paint")))
                .url("https://plugin.houxinlin.com/api/bug");
        ProgressWindowWrapper.newProgressWindowWrapper(project).run(new Task.Backgroundable(project, "Submitting") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    Response response = okHttpClient.newCall(builder.build()).execute();
                    onCommitResponse(response);
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> commitTip.setText((ResourceBundleUtils.getString("commit.fail"))));
                }
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
