package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.action.response.*;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.hxl.plugin.springboot.invoke.utils.file.FileChooseUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class HTTPResponseView extends SimpleToolWindowPanel {
    private byte[] bytes;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel leftResponse = new JPanel(cardLayout);
    private final JPanel rightTool = new JPanel(new BorderLayout());
    private final Map<String, ResponsePage> responsePageMap = new HashMap<>();
    private String currentTypeName = "json";
    private final ToggleManager toggleManager = getNotify();
    private String contentType = "";

    public HTTPResponseView(Project project) {
        super(false);
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new BaseToggleAction("json", AllIcons.Json.Object, toggleManager));
        group.add(new BaseToggleAction("text", MyIcons.TEXT, toggleManager));
        group.add(new BaseToggleAction("image", MyIcons.IMAGE, toggleManager));
        group.add(new BaseToggleAction("html", MyIcons.HTML, toggleManager));
        group.add(new BaseToggleAction("xml", MyIcons.XML, toggleManager));

        DefaultActionGroup toolGroup = new DefaultActionGroup();
        toolGroup.add(new BaseAction("Save", AllIcons.Actions.MenuSaveall) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                String name = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss")) + "." + getDefaultSuffix();
                String storagePath = FileChooseUtils.getSavePath(null, name, e.getProject());
                if (storagePath == null) return;
                if (HTTPResponseView.this.bytes == null) return;
                try {
                    Files.write(Paths.get(storagePath), HTTPResponseView.this.bytes);
                } catch (IOException ex) {
                }
            }
        });
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("bar", group, false);
        ActionToolbar rightToolBar = ActionManager.getInstance().createActionToolbar("bar", toolGroup, false);
        toolbar.setTargetComponent(this);
        setToolbar(toolbar.getComponent());

        responsePageMap.put("json", new JSON(project));
        responsePageMap.put("text", new Text(project));
        responsePageMap.put("image", new Image());
        responsePageMap.put("xml", new XML(project));
        responsePageMap.put("html", new Html());
        for (String key : responsePageMap.keySet()) {
            leftResponse.add(key, ((Component) responsePageMap.get(key)));
        }
        rightTool.add(rightToolBar.getComponent());

        JPanel root = new JPanel(new BorderLayout());
        root.add(leftResponse, BorderLayout.CENTER);
        root.add(rightTool, BorderLayout.EAST);

        add(root);
        switchPage(currentTypeName);
    }

    private String getDefaultSuffix() {
        if (this.contentType == null) return "txt";
        if (contentType.toLowerCase().startsWith("text/html")) return "html";
        if (contentType.toLowerCase().startsWith("application/json")) return "json";
        if (contentType.toLowerCase().startsWith("application/xml")) return "xml";
        if (contentType.toLowerCase().startsWith("text/plain")) return "txt";

        if (contentType.toLowerCase().startsWith("image/jpeg")) return "jpeg";
        if (contentType.toLowerCase().startsWith("image/jpg")) return "jpg";
        if (contentType.toLowerCase().startsWith("image/png")) return  "png";
        if (contentType.toLowerCase().startsWith("image/gif")) return "gif";
        if (contentType.toLowerCase().startsWith("image/bmp")) return  "bmp";
        if (contentType.toLowerCase().startsWith("image/webp")) return "webp";
        if (contentType.toLowerCase().startsWith("image/ico")) return  "ico";
        if (contentType.toLowerCase().startsWith("image")) return "jpg";
        return "txt";
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setResponseData(String contentType, byte[] bytes) {
        this.bytes = bytes;
        this.contentType = contentType;
        responsePageMap.get(currentTypeName).init();
        autoSetType(contentType);
    }

    private void autoSetType(String contentType) {
        if (StringUtils.isEmpty(contentType)) return;
        if (contentType.toLowerCase().startsWith("text/html")) toggleManager.setSelect("html");
        if (contentType.toLowerCase().startsWith("application/json")) toggleManager.setSelect("json");
        if (contentType.toLowerCase().startsWith("application/xml")) toggleManager.setSelect("xml");
        if (contentType.toLowerCase().startsWith("text")) toggleManager.setSelect("text");
        if (contentType.toLowerCase().startsWith("image")) toggleManager.setSelect("image");
    }

    public void switchPage(String name) {
        cardLayout.show(leftResponse, name);
        if (responsePageMap.containsKey(name)) {
            if (bytes != null) {
                responsePageMap.get(name).init();
                currentTypeName = name;
            }
        }
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

    public void reset() {
        this.bytes = new byte[]{0};
        this.toggleManager.setSelect("json");
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
