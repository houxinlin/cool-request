package com.cool.request.view.table;

import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.cool.request.components.http.FormDataInfo;
import com.cool.request.components.http.KeyValue;
import com.cool.request.components.http.net.MediaType;
import com.cool.request.components.http.net.MediaTypes;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.main.IRequestParamManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WWWFormUrlencodedModeFactory extends KeyValueTableModeFactory {
    private final List<ExtractToAnActionButton> extractToAnActionButtons = new ArrayList<>();

    public WWWFormUrlencodedModeFactory(SuggestFactory suggestFactory, IRequestParamManager iRequestParamManager) {
        super(suggestFactory);
        if (iRequestParamManager == null) return;
        extractToAnActionButtons.add(new ExtractToAnActionButton("Extract To") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                DefaultActionGroup defaultActionGroup = new DefaultActionGroup(
                        new ExtractToFormData(iRequestParamManager));

                JBPopupFactory.getInstance().createActionGroupPopup(
                                null, defaultActionGroup, e.getDataContext(), JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                                false, null, 10, null, "popup@RefreshAction")
                        .showUnderneathOf(e.getInputEvent().getComponent());
            }
        });
    }

    @Override
    public ToolbarDecoratorFactory createToolbarDecoratorFactory(TableOperator tablePanel) {
        return new ExtractToToolbarDecoratorFactory(tablePanel, this, extractToAnActionButtons);
    }

    static class ExtractToFormData extends AnAction {
        private final IRequestParamManager iRequestParamManager;

        public ExtractToFormData(IRequestParamManager iRequestParamManager) {
            super(() -> ResourceBundleUtils.getString("extract.to.form.data"), null);
            this.iRequestParamManager = iRequestParamManager;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            iRequestParamManager.stopAllEditor();
            List<KeyValue> urlencodedBody = iRequestParamManager.getUrlencodedBody(RowDataState.all);
            iRequestParamManager.setUrlencodedBody(new ArrayList<>());

            List<FormDataInfo> oldData = iRequestParamManager.getFormData(RowDataState.all);
            oldData.addAll(urlencodedBody.stream()
                    .map(keyValue -> new FormDataInfo(keyValue.getKey(), keyValue.getValue(), "text")).collect(Collectors.toList()));
            iRequestParamManager.setFormData(oldData);

            iRequestParamManager.switchRequestBodyType(new MediaType(MediaTypes.MULTIPART_FORM_DATA));
        }
    }
}
