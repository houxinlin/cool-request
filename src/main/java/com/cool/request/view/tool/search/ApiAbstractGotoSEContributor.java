/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ApiAbstractGotoSEContributor.java is part of Cool Request
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

package com.cool.request.view.tool.search;

import com.cool.request.components.api.scans.SpringMvcControllerScan;
import com.cool.request.components.http.Controller;
import com.cool.request.view.tool.UserProjectManager;
import com.intellij.ide.actions.searcheverywhere.AbstractGotoSEContributor;
import com.intellij.ide.actions.searcheverywhere.FoundItemDescriptor;
import com.intellij.ide.util.PsiElementListCellRenderer;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.ide.util.gotoByName.GotoFileCellRenderer;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFilePathWrapper;
import com.intellij.openapi.vfs.newvfs.VfsPresentationUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.presentation.java.SymbolPresentationUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.Processor;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ApiAbstractGotoSEContributor extends AbstractGotoSEContributor {
    private SpringMvcControllerScan scan;
    private List<ControllerNavigationItem> allController;
    private AnActionEvent event;
    private Project myProject;

    public ApiAbstractGotoSEContributor(@NotNull AnActionEvent event) {
        super(event);
        scan = new SpringMvcControllerScan();
        this.event = event;
        this.myProject = event.getProject();
    }

    @Override
    public @NotNull ListCellRenderer<Object> getElementsRenderer() {
        return new PsiElementListCellRenderer<>() {
            @Override
            public String getElementText(PsiElement element) {
                VirtualFile file = element instanceof PsiFile ? PsiUtilCore.getVirtualFile(element) :
                        element instanceof VirtualFile ? (VirtualFile) element : null;
                if (file != null) {
                    return VfsPresentationUtil.getPresentableNameForUI(element.getProject(), file);
                }

                if (element instanceof NavigationItem) {
                    String name = Optional.ofNullable(((NavigationItem) element).getPresentation())
                            .map(presentation -> presentation.getPresentableText())
                            .orElse(null);
                    if (name != null) return name;
                }

                String name = element instanceof PsiNamedElement ? ((PsiNamedElement) element).getName() : null;
                return StringUtil.notNullize(name, "<unnamed>");
            }

            @Override
            protected int getIconFlags() {
                return super.getIconFlags();
            }

            @Override
            protected @Nullable String getContainerText(PsiElement element, String name) {
                return getContainerTextForLeftComponent(element, name, -1, null);
            }

            @Nullable
            @Override
            protected String getContainerTextForLeftComponent(PsiElement element, String name, int maxWidth, FontMetrics fm) {
                String presentablePath = extractPresentablePath(element);
                String text = ObjectUtils.chooseNotNull(presentablePath, SymbolPresentationUtil.getSymbolContainerText(element));

                if (text == null || text.equals(name)) return null;

                if (text.startsWith("(") && text.endsWith(")")) {
                    text = text.substring(1, text.length() - 1);
                }

                if (presentablePath == null && (text.contains("/") || text.contains(File.separator)) && element instanceof PsiFileSystemItem) {
                    Project project = element.getProject();
                    String basePath = Optional.ofNullable(project.getBasePath())
                            .map(FileUtil::toSystemDependentName)
                            .orElse(null);
                    VirtualFile file = ((PsiFileSystemItem) element).getVirtualFile();
                    if (file != null) {
                        text = FileUtil.toSystemDependentName(text);
                        String filePath = FileUtil.toSystemDependentName(file.getPath());
                        if (basePath != null && FileUtil.isAncestor(basePath, filePath, true)) {
                            text = ObjectUtils.notNull(FileUtil.getRelativePath(basePath, text, File.separatorChar), text);
                        } else {
                            String rootPath = Optional.ofNullable(GotoFileCellRenderer.getAnyRoot(file, project))
                                    .map(root -> FileUtil.toSystemDependentName(root.getPath()))
                                    .filter(root -> basePath != null && FileUtil.isAncestor(basePath, root, true))
                                    .orElse(null);
                            text = rootPath != null
                                    ? ObjectUtils.notNull(FileUtil.getRelativePath(rootPath, text, File.separatorChar), text)
                                    : FileUtil.getLocationRelativeToUserHome(text);
                        }
                    }
                }

                boolean in = text.startsWith("in ");
                if (in) text = text.substring(3);
                String left = in ? "in " : "";
                String adjustedText = left + text;
                if (maxWidth < 0) return adjustedText;

                int fullWidth = fm.stringWidth(adjustedText);
                if (fullWidth < maxWidth) return adjustedText;
                String separator = text.contains("/") ? "/" :
                        SystemInfo.isWindows && text.contains("\\") ? "\\" :
                                text.contains(".") ? "." :
                                        text.contains("-") ? "-" : " ";
                LinkedList<String> parts = new LinkedList<>(StringUtil.split(text, separator));
                int index;
                while (parts.size() > 1) {
                    index = parts.size() / 2 - 1;
                    parts.remove(index);
                    if (fm.stringWidth(left + StringUtil.join(parts, separator) + "...") < maxWidth) {
                        parts.add(index, "...");
                        return left + StringUtil.join(parts, separator);
                    }
                }
                int adjustedWidth = Math.max(adjustedText.length() * maxWidth / fullWidth - 1, left.length() + 3);
                return StringUtil.trimMiddle(adjustedText, adjustedWidth);
            }

            @Nullable
            private String extractPresentablePath(@Nullable PsiElement element) {
                if (element == null) return null;

                PsiFile file = element.getContainingFile();
                if (file != null) {
                    VirtualFile virtualFile = file.getVirtualFile();
                    if (virtualFile instanceof VirtualFilePathWrapper)
                        return ((VirtualFilePathWrapper) virtualFile).getPresentablePath();
                }

                return null;
            }


            @Override
            protected boolean customizeNonPsiElementLeftRenderer(ColoredListCellRenderer renderer, JList list, Object value, int index, boolean selected, boolean hasFocus) {
                if (!(value instanceof NavigationItem)) return false;
                NavigationItem item = (NavigationItem) value;
                Color fgColor = list.getForeground();
                Color bgColor = UIUtil.getListBackground();
                SimpleTextAttributes urlNameAttributes = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, fgColor);
                ItemMatchers itemMatchers = getItemMatchers(list, value);

                ItemPresentation presentation = Objects.requireNonNull(item.getPresentation());
                SpeedSearchUtil.appendColoredFragmentForMatcher(presentation.getPresentableText(), renderer, urlNameAttributes,
                        itemMatchers.nameMatcher, bgColor, selected);
                renderer.setIcon(presentation.getIcon(true));

                String locationString = presentation.getLocationString();
                if (!StringUtil.isEmpty(locationString)) {
                    renderer.append(" " + locationString, new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.GRAY));
                }

                return true;
            }

            @Override
            protected @Nullable DefaultListCellRenderer getRightCellRenderer(Object value) {
                DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (value instanceof Controller) {
                            ((DefaultListCellRenderer) component).setText(((Controller) value).getModuleName());
                        }
                        return component;
                    }
                };
                return defaultListCellRenderer;
            }
        };
    }

    @Override
    protected @NotNull FilteringGotoByModel<?> createModel(@NotNull Project project) {
        return null;
    }


    @Override
    public boolean processSelectedItem(@NotNull Object selected, int modifiers, @NotNull String searchText) {
        return super.processSelectedItem(selected, modifiers, searchText);
    }

    @Override
    public Object getDataForItem(@NotNull Object element, @NotNull String dataId) {
        return null;
    }


    @Override
    public @NotNull String getSearchProviderId() {
        return ApiAbstractGotoSEContributor.class.getSimpleName();
    }

    @Override
    public boolean isEmptyPatternSupported() {
        return true;
    }


    @Override
    public boolean showInFindResults() {
        return false;
    }

    @Override
    public boolean isDumbAware() {
        return DumbService.isDumb(myProject);
    }


    @Override
    public void fetchWeightedElements(@NotNull String pattern, @NotNull ProgressIndicator progressIndicator,
                                      @NotNull Processor<? super FoundItemDescriptor<Object>> consumer) {
        if (isDumbAware()) {
            return;
        }
        progressIndicator.start();
        if (allController == null || allController.size() == 0) {
            try {
                UserProjectManager userProjectManager = UserProjectManager.getInstance(myProject);
                allController = userProjectManager.getController()
                        .stream()
                        .map(controller -> new ControllerNavigationItem(controller, myProject))
                        .collect(Collectors.toList());
                if (allController == null || allController.isEmpty()) {
                    allController = ApplicationManager.getApplication().runReadAction(
                                    (ThrowableComputable<List<Controller>, Throwable>) () ->
                                            scan.scan(event.getProject())
                            ).stream()
                            .map(controller -> new ControllerNavigationItem(controller, myProject))
                            .collect(Collectors.toList());
                }
            } catch (Throwable e) {
            }
        }

        MinusculeMatcher matcher = NameUtil.buildMatcher("*" + removeParam(pattern) + "*", NameUtil.MatchingCaseSensitivity.NONE);
        for (Controller controller : allController) {
            if (matcher.matches(controller.getUrl())) {
                if (!consumer.process(new FoundItemDescriptor<>(controller, 0))) {
                    return;
                }
            }
        }

    }

    private static String removeParam(String url) {
        try {
            URL newUrl = new URL(url);
            return newUrl.getPath();
        } catch (MalformedURLException e) {
        }
        if (url.contains("?")) url = url.substring(0, url.indexOf('?'));
        return url;
    }

    @Override
    public boolean isShownInSeparateTab() {
        return true;
    }


    @Override
    public @NotNull @Nls String getGroupName() {
        return "Cool Request";
    }


    @Override
    public int getSortWeight() {
        return 888;
    }
}
