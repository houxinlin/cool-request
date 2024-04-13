/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ApipostConfigUI.java is part of Cool Request
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

package com.cool.request.plugin.apipost;

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.common.state.ThirdPartyPersistent;
import com.cool.request.utils.*;
import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.ui.AnimatedIcon;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class ApipostConfigUI implements ConfigurableUi<ApipostSetting> {
    private JPanel contentPane;
    private JTextField hostTextField;
    private JTextField tokenTextField;
    private JLabel resultTip;
    private JButton checkTokenButton;
    private JLabel help;

    public ApipostConfigUI(Project project) {
        hostTextField.setText(ThirdPartyPersistent.getInstance().apipostHost);
        tokenTextField.setText(ThirdPartyPersistent.getInstance().apipostToken);
        help.setIcon(CoolRequestIcons.HELP);
        help.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                WebBrowseUtils.browse("https://coolrequest.dev/docs/three-part/apipost");
            }
        });
        checkTokenButton.addActionListener(e -> {

            try {
                preCheck();
                checkTokenButton.setIcon(new AnimatedIcon.Default());
                resultTip.setText("");
                ProgressManager.getInstance().run(new Task.Backgroundable(project, "Token检测中") {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        try {
                            boolean checked = new ApipostAPI().checkToken(hostTextField.getText(), tokenTextField.getText());
                            SwingUtilities.invokeLater(() -> resultTip.setText(checked ? "Success" : "Invalid Token"));
                        } catch (IOException ex) {
                            ExceptionDialogHandlerUtils.handlerException(ex);
                        } finally {
                            checkTokenButton.setIcon(null);
                        }
                    }
                });
            } catch (IllegalArgumentException illegalArgumentException) {
                MessagesWrapperUtils.showErrorDialog(illegalArgumentException.getMessage(), "提示");
            }

        });
    }

    private void preCheck() throws IllegalArgumentException {
        if (!UrlUtils.isURL(hostTextField.getText())) throw new IllegalArgumentException("无效URL");
        if (StringUtils.isEmpty(tokenTextField.getText())) throw new IllegalArgumentException("请输入Token");
    }

    @Override
    public void reset(@NotNull ApipostSetting settings) {

    }

    @Override
    public boolean isModified(@NotNull ApipostSetting settings) {
        return !(StringUtils.isEquals(hostTextField.getText(), settings.getApipostHost()))
                || (!StringUtils.isEquals(tokenTextField.getText(), settings.getApipostApiToken()));
    }

    @Override
    public void apply(@NotNull ApipostSetting settings) throws ConfigurationException {
        ThirdPartyPersistent.getInstance().apipostHost = hostTextField.getText();
        ThirdPartyPersistent.getInstance().apipostToken = tokenTextField.getText();

    }

    @Override
    public @NotNull JComponent getComponent() {
        return contentPane;
    }

}
