package com.cool.request.view.page;

import com.cool.request.agent.trace.TraceFrame;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.components.CoolRequestPluginDisposable;
import com.cool.request.components.http.Controller;
import com.cool.request.trace.ClassInfoMap;
import com.cool.request.utils.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.ui.SimpleTextAttributes.STYLE_BOLD;
import static com.intellij.ui.SimpleTextAttributes.STYLE_PLAIN;

public class TracePreviewView extends SimpleToolWindowPanel {
    private com.intellij.ui.treeStructure.Tree tree = new com.intellij.ui.treeStructure.Tree();

    private Controller controller;
    private Project project;

    public void setController(Controller controller) {
        this.controller = controller;
        setTraceFrame(new ArrayList<>());
    }

    private ActionGroup getPopupActions() {
        DefaultActionGroup defaultActionGroup = new DefaultActionGroup();
        defaultActionGroup.add(new GoToCodeAnAction());
        defaultActionGroup.add(new GoToGithubSource());
        return defaultActionGroup;
    }


    public TracePreviewView(Project project) {
        super(false);
        setContent(new JBScrollPane(tree));
        this.project = project;
        tree.setCellRenderer(new TraceColoredTreeCellRenderer());
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                invokePopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                invokePopup(e);
            }

            public void mouseClicked(MouseEvent e) {
                invokePopup(e);
            }

            private void invokePopup(final MouseEvent e) {
                if (e.isPopupTrigger() && insideTreeItemsArea(e)) {
                    JPopupMenu component = ActionManager.getInstance()
                            .createActionPopupMenu("right", getPopupActions())
                            .getComponent();
                    component.show(e.getComponent(), e.getX(), e.getY());
                }

            }

            private boolean insideTreeItemsArea(MouseEvent e) {
                Rectangle rowBounds = tree.getRowBounds(tree.getRowCount() - 1);
                if (rowBounds == null) {
                    return false;
                }
                double lastItemBottomLine = rowBounds.getMaxY();
                return e.getY() <= lastItemBottomLine;
            }
        });
        MessageBusConnection messageBusConnection = project.getMessageBus().connect();

        messageBusConnection.subscribe(CoolRequestIdeaTopic.TRACE_FINISH,
                (CoolRequestIdeaTopic.TraceFinishListener) traceFrames -> {
                    if (controller == null) return;
                    if (traceFrames.size() > 0 &&
                            traceFrames.get(0).getClassName().equals(controller.getJavaClassName())) {
                        setTraceFrame(traceFrames);
                    }
                });
        Disposer.register(CoolRequestPluginDisposable.getInstance(project), messageBusConnection);
    }

    public void setTraceFrame(List<TraceFrame> traceFrames) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        root.removeAllChildren();
        root.setUserObject((traceFrames != null ? traceFrames.size() : 0) + " trace");
        ((DefaultTreeModel) tree.getModel()).reload();

        if (traceFrames == null || traceFrames.isEmpty()) return;
        TreeNode<TraceFrame> treeNode = new BinaryTreeBuilder<TraceFrame>().buildTree(traceFrames,
                TraceFrame::getMethodId,
                TraceFrame::getParentMethodId);
        if (treeNode == null || treeNode.getChildren() == null) return;
        for (TreeNode<TraceFrame> child : treeNode.getChildren()) {
            TraceNode newNode = new TraceNode(child.getTreeData());
            root.add(newNode);
            buildNode(newNode, child.getChildren());
        }
        SwingUtilities.invokeLater(() -> {
            ((DefaultTreeModel) tree.getModel()).reload();
            TreeUtil.expandAll(tree);
        });

    }

    private void buildNode(DefaultMutableTreeNode parent, List<TreeNode<TraceFrame>> traceFrames) {
        if (traceFrames == null) return;
        for (TreeNode<TraceFrame> traceFrame : traceFrames) {
            TraceNode newNode = new TraceNode(traceFrame.getTreeData());
            parent.add(newNode);
            buildNode(newNode, traceFrame.getChildren());
        }
    }

    private SimpleTextAttributes getSimpleTextAttributes(long time) {
        if (time < SettingPersistentState.getInstance().getState().timeConsumptionThreshold)
            return new SimpleTextAttributes(STYLE_BOLD, CoolRequestConfigConstant.Colors.GREEN);
        return new SimpleTextAttributes(STYLE_BOLD, CoolRequestConfigConstant.Colors.RED);
    }

    class TraceNode extends DefaultMutableTreeNode {
        public TraceNode(Object userObject) {
            super(userObject);
        }
    }

    private class GoToGithubSource extends AnAction {
        public GoToGithubSource() {
            super("Framework site");
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            TraceFrame traceFrame = getSelectTraceFrame();
            if (traceFrame == null) return;
            String site = ClassInfoMap.INSTANCE.getClassSite(traceFrame.getClassName());
            if (StringUtils.isEmpty(site)) return;
            WebBrowseUtils.browse(site);
        }
    }

    private TraceFrame getSelectTraceFrame() {
        TreePath selectedPath = TreeUtil.getSelectedPathIfOne(tree);
        if (selectedPath == null) return null;
        Object lastPathComponent = selectedPath.getLastPathComponent();
        if (lastPathComponent instanceof TraceNode) {
            TraceNode traceNode = (TraceNode) lastPathComponent;
            return ((TraceFrame) traceNode.getUserObject());
        }
        return null;
    }

    private class GoToCodeAnAction extends AnAction {
        public GoToCodeAnAction() {
            super("Go to code");
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            TraceFrame traceFrame = getSelectTraceFrame();
            if (traceFrame == null) return;
            String methodName = traceFrame.getMethodName();
            if (methodName.indexOf("(") != -1) {
                NavigationUtils.jumpToCode(project,
                        traceFrame.getClassName(),
                        methodName.substring(0, methodName.indexOf("(")));
            }
        }
    }

    public class TraceColoredTreeCellRenderer extends ColoredTreeCellRenderer {
        @Override
        public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            if (value instanceof TraceNode) {
                TraceNode traceNode = (TraceNode) value;
                TraceFrame traceFrame = (TraceFrame) traceNode.getUserObject();
                if (traceFrame != null) {
                    if (traceFrame.getCallCount() > 1) {
                        append("+" + traceFrame.getCallCount() + " ", new SimpleTextAttributes(STYLE_BOLD, CoolRequestConfigConstant.Colors.BLUE));
                    }
                    long time = (traceFrame.getExitTimeStamp() - traceFrame.getEnterTimeStamp());
                    append("[ " + time + " ] ms ", getSimpleTextAttributes(time));
                    append(traceFrame.getClassName());
                    append(".");
                    append(traceFrame.getMethodName());
                    setIcon(ClassInfoMap.INSTANCE.getClassIcon(traceFrame.getClassName()));
                }
            } else {
                append(value.toString());
            }

        }
    }

}
