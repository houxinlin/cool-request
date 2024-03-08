package com.cool.request.plugin.apipost;

import com.cool.request.common.state.ThirdPartyPersistent;
import com.cool.request.utils.MessagesWrapperUtils;
import com.cool.request.utils.StringUtils;
import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;

public class ApipostConfigUI implements ConfigurableUi<ApipostSetting> {
    private JPanel contentPane;
    private JLabel host;
    private JTextField hostTextField;
    private JTextField tokenTextField;
    private JLabel token;
    private JLabel resultTip;
    private JButton checkTokenButton;
    private JButton buttonOK;
    private JButton buttonCancel;
    private Project project;

    public ApipostConfigUI(Project project) {
        this.project = project;
        hostTextField.setText(ThirdPartyPersistent.getInstance().apipostHost);
        tokenTextField.setText(ThirdPartyPersistent.getInstance().apipostToken);
        checkTokenButton.addActionListener(e -> ProgressManager.getInstance().run(new Task.Backgroundable(project, "Token检测中") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    boolean checked = new ApipostAPI().checkToken(hostTextField.getText(), tokenTextField.getText());
                    SwingUtilities.invokeLater(() -> resultTip.setText(checked ? "Success" : "Invalid Token"));
                } catch (IOException ex) {
                    MessagesWrapperUtils.showErrorDialog("网络不可达", "提示");
                }
            }
        }));
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
