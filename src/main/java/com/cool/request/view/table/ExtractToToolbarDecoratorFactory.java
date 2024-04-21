package com.cool.request.view.table;

import com.intellij.ui.AnActionButton;

import java.util.ArrayList;
import java.util.List;

public class ExtractToToolbarDecoratorFactory extends DefaultToolbarDecoratorFactory {
    private List<ExtractToAnActionButton> actionButtons;

    public ExtractToToolbarDecoratorFactory(TableOperator tableOperator,
                                            TableModeFactory<?> tableModeFactory,
                                            List<ExtractToAnActionButton> actionButtons) {
        super(tableOperator, tableModeFactory);
        this.actionButtons = actionButtons;
    }

    @Override
    public List<AnActionButton> getExtraActions() {
        List<AnActionButton> oldAction = new ArrayList<>(super.getExtraActions());
        oldAction.addAll(actionButtons);
        return oldAction;
    }
}
