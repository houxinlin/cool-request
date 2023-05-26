package com.hxl.plugin.springboot.invoke.view.browse;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

public class JsonBrowse extends DialogWrapper {
    private JTree jTree =new JTree();
    private final String json;
    public JsonBrowse(String json) {
        super(false);
        this.json = json;
        setModal(false);
        setTitle(ResourceBundleUtils.getString("response"));
        setOKButtonText(ResourceBundleUtils.getString("close.and.copy"));
        setSize(400,400);
        init();

    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        updateModel();
        return new JScrollPane(jTree);
    }
    @Override
    protected void doOKAction() {
        super.doOKAction();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 创建ObjectMapper对象
        ObjectMapper objectMapper =ObjectMappingUtils.getInstance();
        try {
            JsonFactory jsonFactory = objectMapper.getFactory();
            JsonParser jsonParser = jsonFactory.createParser(this.json);
            Object jsonObject = objectMapper.readTree(jsonParser);
            ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            String formattedJson = objectWriter.writeValueAsString(jsonObject);
            StringSelection selection = new StringSelection(formattedJson);
            clipboard.setContents(selection, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
    }
    private void updateModel() {
        try {
            ObjectMapper mapper = ObjectMappingUtils.getInstance();
            JsonNode rootJsonNode = mapper.readTree(json);
            DefaultMutableTreeNode rootNode = buildTree("root", rootJsonNode);
            jTree.setModel(new DefaultTreeModel(rootNode));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
    private DefaultMutableTreeNode buildTree(String name, JsonNode node) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(name);
        Iterator<Entry<String, JsonNode>> it = node.fields();
        while (it.hasNext()) {
            Entry<String, JsonNode> entry = it.next();
            treeNode.add(buildTree(entry.getKey(), entry.getValue()));
        }

        if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                JsonNode child = node.get(i);
                if (child.isValueNode())
                    treeNode.add(new DefaultMutableTreeNode(child.asText()));
                else
                    treeNode.add(buildTree(String.format("[%d]", i), child));
            }
        } else if (node.isValueNode()) {
            treeNode.add(new DefaultMutableTreeNode(node.asText()));
        }
        return treeNode;
    }


}