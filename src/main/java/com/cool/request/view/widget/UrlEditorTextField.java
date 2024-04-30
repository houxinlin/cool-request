/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * UrlEditorTextField.java is part of Cool Request
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

package com.cool.request.view.widget;

import com.cool.request.common.bean.EmptyEnvironment;
import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.cool.request.components.http.KeyValue;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.url.MultiValueMap;
import com.cool.request.utils.url.UriComponents;
import com.cool.request.utils.url.UriComponentsBuilder;
import com.cool.request.view.main.IRequestParamManager;
import com.cool.request.view.table.RowDataState;
import com.cool.request.view.tool.provider.RequestEnvironmentProvideImpl;
import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.actions.SimplePasteAction;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.EditorTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlEditorTextField extends EditorTextField {
    private static final TextAttributes PATH_HIGHLIGHTER = new TextAttributes();
    private static final TextAttributes HOST_HIGHLIGHTER = new TextAttributes();
    private IRequestParamManager iRequestParamManager;

    public UrlEditorTextField(Project project, IRequestParamManager iRequestParamManager) {
        super(createCustomDocument(), project, FileTypes.PLAIN_TEXT);
        this.iRequestParamManager = iRequestParamManager;
        initUrlEditorTextField();
    }


    @Override
    protected @NotNull EditorEx createEditor() {
        EditorEx editor = super.createEditor();
        SimplePasteAction simplePasteAction = new SimplePasteAction();
        simplePasteAction.getTemplatePresentation().setText("Paste");
        simplePasteAction.getTemplatePresentation().setIcon(CoolRequestIcons.PASTE);
        editor.installPopupHandler(event -> {
            DefaultActionGroup group = new DefaultActionGroup();
            group.add(new ExtractQuery());
            group.add(simplePasteAction);
            event.consume();
            MouseEvent mouseEvent = event.getMouseEvent();
            JPopupMenu popupMenu = ActionManager.getInstance().createActionPopupMenu(ActionPlaces.EDITOR_POPUP, group).getComponent();
            popupMenu.show(UrlEditorTextField.this, mouseEvent.getX(), mouseEvent.getY());
            return true;
        });
        editor.setHighlighter(HighlighterFactory.createHighlighter(getProject(), FileTypes.PLAIN_TEXT));
        editor.setEmbeddedIntoDialogWrapper(true);
        return editor;
    }

    private static Document createCustomDocument() {
        return EditorFactory.getInstance().createDocument(StringUtil.convertLineSeparators(""));
    }

    private void initUrlEditorTextField() {
        PATH_HIGHLIGHTER.setForegroundColor(Color.decode("#1F9FC6"));
        HOST_HIGHLIGHTER.setFontType(HOST_HIGHLIGHTER.getFontType() ^ Font.ITALIC);
        addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                highlighterUrl();
            }
        });
    }

    private void highlighterUrl() {
        Editor editor = getEditor();
        if (editor == null) return;
        MarkupModel markupModel = editor.getMarkupModel();
        markupModel.removeAllHighlighters();

        String patternString = "\\{([^}]*)\\}";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(getText());

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            markupModel.addRangeHighlighter(start, end, 0, PATH_HIGHLIGHTER, HighlighterTargetArea.EXACT_RANGE);
        }
        RequestEnvironment selectRequestEnvironment = RequestEnvironmentProvideImpl.getInstance(getProject()).getSelectRequestEnvironment();
        if (!(selectRequestEnvironment instanceof EmptyEnvironment)) {
            String hostAddress = selectRequestEnvironment.getHostAddress();
            if (getText().startsWith(hostAddress)) {
                markupModel.addRangeHighlighter(0, hostAddress.length(), 0, HOST_HIGHLIGHTER, HighlighterTargetArea.EXACT_RANGE);
            }
        }
        invalidate();
    }

    class ExtractQuery extends AnAction {
        public ExtractQuery() {
            super(() -> ResourceBundleUtils.getString("extract.param"), KotlinCoolRequestIcons.INSTANCE.getEXPORT().invoke());
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            try {
                UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(getText());
                UriComponents uriComponents = uriComponentsBuilder.build();
                MultiValueMap<String, String> queryParams = uriComponents.getQueryParams();
                List<KeyValue> newParam = new ArrayList<>(iRequestParamManager.getUrlParam(RowDataState.all));
                for (String key : queryParams.keySet()) {
                    List<String> values = queryParams.get(key);
                    for (String value : values) {
                        newParam.add(new KeyValue(key, value));
                    }
                }
                iRequestParamManager.setUrlParam(newParam);
                iRequestParamManager.setUrl(uriComponentsBuilder.query(null).build().toUriString());
                iRequestParamManager.switchToURlParamPage();
            } catch (Exception ignored) {
            }
        }
    }

}
