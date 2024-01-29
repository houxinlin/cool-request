package com.cool.request.action.actions;

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.component.http.net.FormDataInfo;
import com.cool.request.component.http.net.KeyValue;
import com.cool.request.component.http.net.MediaTypes;
import com.cool.request.lib.curl.ArgumentHolder;
import com.cool.request.lib.curl.BasicCurlParser;
import com.cool.request.lib.curl.StringArgumentHolder;
import com.cool.request.utils.MediaTypeUtils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.utils.UrlUtils;
import com.cool.request.view.dialog.BigInputDialog;
import com.cool.request.view.main.IRequestParamManager;
import com.cool.request.view.tool.ProviderManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ImportCurlParamAnAction extends BaseAnAction {
    public ImportCurlParamAnAction(Project project) {
        super(project, () -> "curl", CoolRequestIcons.CURL);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        importParam();
    }

    public void importParam() {
        BigInputDialog bigInputDialog = new BigInputDialog(getProject(), ResourceBundleUtils.getString("import.curl.tip"));
        bigInputDialog.show();

        try {
            BasicCurlParser.Request parse = new BasicCurlParser().parse(bigInputDialog.getValue());

            //找到参数管理器，设置header、formdata、json参数
            ProviderManager.findAndConsumerProvider(IRequestParamManager.class, getProject(), iRequestParamManager -> {
                iRequestParamManager.importCurl(bigInputDialog.getValue());
            });
        } catch (IllegalArgumentException exception) {
            Messages.showErrorDialog("Unable to parse parameters", "Tip");
        }

    }

}
