package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.action.response.*;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import icons.MyIcons;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HTTPResponseView extends SimpleToolWindowPanel {
    private byte[] bytes;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel root = new JPanel(cardLayout);
    private final Map<String, ResponsePage> responsePageMap = new HashMap<>();
    private String currentTypeName = "json";


    public void setResponseData(byte[] bytes) {
        this.bytes = bytes;
        responsePageMap.get(currentTypeName).init();
//        switchPage("json");
    }

    public void switchPage(String name) {
        cardLayout.show(root, name);
        if (responsePageMap.containsKey(name)) {
            if (bytes != null) {
                responsePageMap.get(name).init();
                currentTypeName = name;
            }
        }
    }

    public HTTPResponseView(Project project) {
        super(false);

        DefaultActionGroup group = new DefaultActionGroup();
        ToggleManager toggleManager = getNotify();

        group.add(new BaseToggleAction("json", AllIcons.Json.Object, toggleManager));
        group.add(new BaseToggleAction("text", MyIcons.TEXT, toggleManager));
        group.add(new BaseToggleAction("image", MyIcons.IMAGE, toggleManager));
        group.add(new BaseToggleAction("html", MyIcons.HTML, toggleManager));
        group.add(new BaseToggleAction("xml", MyIcons.XML, toggleManager));

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("bar", group, false);
        toolbar.setTargetComponent(this);
        setToolbar(toolbar.getComponent());

        responsePageMap.put("json", new JSON(project));
        responsePageMap.put("text", new Text(project));
        responsePageMap.put("image", new Image());
        responsePageMap.put("xml", new XML(project));
        responsePageMap.put("html", new Html());
        for (String key : responsePageMap.keySet()) {
            root.add(key, ((Component) responsePageMap.get(key)));
        }
        add(root);
        switchPage(currentTypeName);
    }

    private ToggleManager getNotify() {
        Map<String, Boolean> selectMap = new HashMap<>();
        selectMap.put("json", true);
        selectMap.put("text", false);
        selectMap.put("image", false);
        selectMap.put("xml", false);
        selectMap.put("html", false);
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

    class XML extends XmlParamRequestBodyPage implements ResponsePage {

        public XML(Project project) {
            super(project);
        }

        @Override
        public void init() {
            setText(new String(bytes));
        }
    }

    class Html extends JScrollPane implements ResponsePage {
        private final JEditorPane jEditorPane = new JEditorPane();
        public Html() {
            jEditorPane.setContentType("text/html");
            jEditorPane.setEditable(false);
            setViewportView(jEditorPane);
        }

        @Override
        public void init() {
            jEditorPane.setText(new String(bytes));
        }
    }

    class Image extends JPanel implements ResponsePage {
        private BufferedImage image;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image == null) return;
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            if (imageHeight < panelWidth && imageHeight < panelHeight) {

                g.drawImage(image, (panelWidth / 2) - (imageWidth / 2), (panelHeight / 2) - (imageHeight / 2), imageWidth, imageHeight, this);
                return;
            }
            double scaleX = (double) panelWidth / imageWidth;
            double scaleY = (double) panelHeight / imageHeight;
            double scale = Math.min(scaleX, scaleY);

            int scaledWidth = (int) (imageWidth * scale);
            int scaledHeight = (int) (imageHeight * scale);

            int x = (panelWidth - scaledWidth) / 2;
            int y = (panelHeight - scaledHeight) / 2;

            g.drawImage(image, x, y, scaledWidth, scaledHeight, this);
        }

        @Override
        public void init() {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            try {
                this.image = ImageIO.read(inputStream);
                repaint();
            } catch (IOException ignored) {
            }
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
