package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.action.response.ImageResponseAction;
import com.hxl.plugin.springboot.invoke.action.response.JSONResponseAction;
import com.hxl.plugin.springboot.invoke.action.response.ToggleManager;
import com.hxl.plugin.springboot.invoke.action.response.TextResponseAction;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class HTTPResponseView extends SimpleToolWindowPanel {
    private byte[] bytes;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel root = new JPanel(cardLayout);
    private final Map<String, ResponsePage> responsePageMap = new HashMap<>();

    public void setResponseData(byte[] bytes) {
        this.bytes = bytes;
    }

    public void switchPage(String name) {
        cardLayout.show(root, name);
        if (responsePageMap.containsKey(name)) {
            if (bytes != null) {
                responsePageMap.get(name).init();
            }
        }
    }

    public HTTPResponseView(Project project) {
        super(false);

        DefaultActionGroup group = new DefaultActionGroup();
        ToggleManager toggleManager = getNotify();

        group.add(new JSONResponseAction("json", AllIcons.Actions.Copy, toggleManager));
        group.add(new ImageResponseAction("image", AllIcons.Actions.AddFile, toggleManager));
        group.add(new TextResponseAction("text", AllIcons.Actions.DeleteTag, toggleManager));

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("bar", group, false);
        toolbar.setTargetComponent(this);
        setToolbar(toolbar.getComponent());

        responsePageMap.put("json", new JSON(project));
        responsePageMap.put("text", new Text(project));

        for (String key : responsePageMap.keySet()) {
            root.add(key, ((Component) responsePageMap.get(key)));
        }
        add(root);
        switchPage("json");
    }

    private  ToggleManager getNotify() {
        Map<String, Boolean> selectMap = new HashMap<>();
        selectMap.put("json", true);
        selectMap.put("text", false);
        selectMap.put("image", false);
        return new ToggleManager() {
            @Override
            public void setSelect(String name) {
                selectMap.replaceAll((s, v) -> false);
                selectMap.put(name, true);
                switchPage(name);
            }
            @Override
            public boolean isSelected(String name) {
                return selectMap.get(name);
            }
        };
    }

    @Override
    public boolean isCycleRoot() {
        return false;
    }

    interface ResponsePage {
        void init();
    }

    class JSON extends JSONRequestBodyPage implements ResponsePage {

        public JSON(Project project) {
            super(project);
        }

        @Override
        public void init() {
            setText(ObjectMappingUtils.format(new String(bytes)));
        }
    }
    class Text extends RawParamRequestBodyPage implements ResponsePage {

        public Text(Project project) {
            super(project);
        }

        @Override
        public void init() {
            setText(new String(bytes));
        }
    }

}
