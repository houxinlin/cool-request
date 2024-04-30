package com.cool.request.view.table;

import com.cool.request.components.http.FormDataInfo;
import com.cool.request.components.http.KeyValue;
import com.cool.request.components.http.net.MediaType;
import com.cool.request.components.http.net.MediaTypes;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.main.IRequestParamManager;
import com.cool.request.view.page.cell.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.cool.request.utils.Constant.EMPTY_STRING;

public class FormDataModeFactory implements TableModeFactory<FormDataInfo> {
    private final Project project;
    private final List<ExtractToAnActionButton> extractToAnActionButtons = new ArrayList<>();

    public FormDataModeFactory(Project project, IRequestParamManager iRequestParamManager) {
        this.project = project;
        if (iRequestParamManager == null) return;
        extractToAnActionButtons.add(new ExtractToAnActionButton("Extract To") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                DefaultActionGroup defaultActionGroup = new DefaultActionGroup(
                        new ExtractToWWWFormUrlencoded(iRequestParamManager));

                JBPopupFactory.getInstance().createActionGroupPopup(
                                null, defaultActionGroup, e.getDataContext(), JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                                false, null, 10, null, "popup@RefreshAction")
                        .showUnderneathOf(e.getInputEvent().getComponent());
            }
        });
    }

    @Override
    public List<Column> createColumn(TableOperator tableOperator) {
        return List.of(
                new ColumnImpl(EMPTY_STRING, tableOperator.getTable().getDefaultEditor(Boolean.class),
                        tableOperator.getTable().getDefaultRenderer(Boolean.class), 30),

                new ColumnImpl("Key", new DefaultTextCellEditable(), new DefaultTextCellRenderer()),
                new ColumnImpl("Value", new FormDataRequestBodyValueCellEditor(tableOperator.getTable(), project),
                        new FormDataRequestBodyValueRenderer()),

                new ColumnImpl("Type", new FormDataRequestBodyComboBoxEditor(tableOperator.getTable()),
                        new FormDataRequestBodyComboBoxRenderer(tableOperator.getTable()), 140),

                new ColumnImpl(EMPTY_STRING, new TableCellAction.TableDeleteButtonCellEditor(e -> {
                    tableOperator.deleteSelectRow();
                }), new TableCellAction.TableDeleteButtonRenderer(), 80));
    }

    @Override
    public Object[] createNewEmptyRow() {
        return new Object[]{true, "", "", "text", ""};
    }

    @Override
    public Object[] createNewRowWithData(FormDataInfo formDataInfo) {
        return new Object[]{true, formDataInfo.getName(), formDataInfo.getValue(), formDataInfo.getType(), ""};
    }

    @Override
    public ToolbarDecoratorFactory createToolbarDecoratorFactory(TableOperator tablePanel) {
        return new ExtractToToolbarDecoratorFactory(tablePanel, this, extractToAnActionButtons);
    }

    static class ExtractToWWWFormUrlencoded extends AnAction {
        private final IRequestParamManager iRequestParamManager;

        public ExtractToWWWFormUrlencoded(IRequestParamManager iRequestParamManager) {
            super(() -> ResourceBundleUtils.getString("extract.to.urlencoded.data"));
            this.iRequestParamManager = iRequestParamManager;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            iRequestParamManager.stopAllEditor();
            List<FormDataInfo> formData = iRequestParamManager.getFormData(RowDataState.all);
            iRequestParamManager.setFormData(new ArrayList<>());

            List<KeyValue> urlencodedBody = iRequestParamManager.getUrlencodedBody(RowDataState.all);
            urlencodedBody.addAll(formData.stream()
                    .map(formDataInfo -> new KeyValue(formDataInfo.getName(), formDataInfo.getValue())).collect(Collectors.toList()));
            iRequestParamManager.setUrlencodedBody(urlencodedBody);

            iRequestParamManager.switchRequestBodyType(new MediaType(MediaTypes.APPLICATION_WWW_FORM));
        }
    }
}
