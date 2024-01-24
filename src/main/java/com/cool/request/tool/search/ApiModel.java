package com.cool.request.tool.search;

import com.cool.request.bean.components.controller.Controller;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ApiModel  extends FilteringGotoByModel<Controller> {
    public ApiModel(@NotNull Project project, ChooseByNameContributor @NotNull [] contributors) {
        super(project, contributors);
    }

    @Override
    protected @Nullable Controller filterValueFor(NavigationItem item) {
        return null;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) String getPromptText() {
        return null;
    }

    @Override
    public @NotNull @NlsContexts.Label String getNotInMessage() {
        return null;
    }

    @Override
    public @NotNull @NlsContexts.Label String getNotFoundMessage() {
        return null;
    }

    @Override
    public @Nullable @NlsContexts.Label String getCheckBoxName() {
        return null;
    }

    @Override
    public boolean loadInitialCheckBoxState() {
        return false;
    }

    @Override
    public void saveInitialCheckBoxState(boolean state) {

    }

    @Override
    public String @NotNull [] getSeparators() {
        return new String[0];
    }

    @Override
    public @Nullable String getFullName(@NotNull Object element) {
        return null;
    }

    @Override
    public boolean willOpenEditor() {
        return false;
    }
}
