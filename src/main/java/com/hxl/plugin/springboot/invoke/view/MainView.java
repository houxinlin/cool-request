package com.hxl.plugin.springboot.invoke.view;

import com.google.gson.Gson;
import com.hxl.plugin.springboot.invoke.bean.ProjectRequestBean;
import com.hxl.plugin.springboot.invoke.bean.RequestMappingInvokeBean;
import com.hxl.plugin.springboot.invoke.bean.ScheduledInvokeBean;
import com.hxl.plugin.springboot.invoke.invoke.InvokeResult;
import com.hxl.plugin.springboot.invoke.invoke.ScheduledInvoke;
import com.hxl.plugin.springboot.invoke.net.PluginCommunication;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.utils.TextFieldTextChangedListener;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MainView implements PluginCommunication.MessageCallback {
    private final PlaceholderTextField controllerSearchTextField = new PlaceholderTextField();
    private final PlaceholderTextField scheduledSearchTextField = new PlaceholderTextField();
    private final NotificationGroup NOTIFICATION_GROUP = new NotificationGroup("com.hxl.plugin.scheduled-invoke", NotificationDisplayType.BALLOON, true);

    private final PluginCommunication pluginCommunication = new PluginCommunication(this);
    private final JList<String[]> controllerJList = new JList<>();

    private ProjectRequestBean projectRequestBean;
    private List<RequestMappingInvokeBean> requestMappingFilterResult;
    private List<ScheduledInvokeBean> scheduledFilterResult;
    private final JList<ScheduledInvokeBean> scheduleJList = new JList<>(new DefaultListModel<>());
    private Project project;
    private static final String BEAN_INFO = "bean_info";
    private static final String RESPONSE_INFO = "response_info";

    public MainView(@NotNull Project project) {
        controllerJList.setBorder(null);
        controllerJList.setCellRenderer(new JListControllerCellRenderer());
        controllerJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = controllerJList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        new InvokeDialog(requestMappingFilterResult.get(index), projectRequestBean.getPort(), invokeResult -> notification(invokeResult.getMessage())).show();
                    }
                }
            }
        });
        scheduleJList.setBorder(null);
        //调度器的单击时间
        scheduleJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleJList.setCellRenderer(new JListScheduledCellRenderer());
        scheduleJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = scheduleJList.getSelectedIndex();
                    if (index >= 0) {
                        ScheduledInvoke.InvokeData invokeData = new ScheduledInvoke.InvokeData(scheduledFilterResult.get(index).getId());
                        InvokeResult invoke = new ScheduledInvoke(projectRequestBean.getPort()).invoke(invokeData);
                        notification(invoke.getMessage());
                    }
                }
            }
        });

        controllerSearchTextField.setPlaceholder("search...");
        controllerSearchTextField.setOpaque(false);
        scheduledSearchTextField.setPlaceholder("search...");
        scheduledSearchTextField.setOpaque(false);

        controllerSearchTextField.getDocument().addDocumentListener(new TextFieldTextChangedListener() {
            @Override
            protected void textChanged() {
                String text = controllerSearchTextField.getText();
                if (projectRequestBean == null || projectRequestBean.getController() == null) return;
                setController(controllerFilter(projectRequestBean.getController(), text));
            }
        });
        scheduledSearchTextField.getDocument().addDocumentListener(new TextFieldTextChangedListener() {
            @Override
            public void textChanged() {
                String text = scheduledSearchTextField.getText();
                if (projectRequestBean == null || projectRequestBean.getScheduled() == null) return;
                setScheduleData(scheduledFilter(projectRequestBean.getScheduled(), text));
            }
        });
    }

    private List<ScheduledInvokeBean> scheduledFilter(List<ScheduledInvokeBean> source, String searchText) {
        Set<ScheduledInvokeBean> result = new HashSet<>();
        result.addAll(source.stream().filter(scheduledInvokeBean -> scheduledInvokeBean.getClassName().contains(searchText)).collect(Collectors.toList()));
        result.addAll(source.stream().filter(scheduledInvokeBean -> scheduledInvokeBean.getMethodName().contains(searchText)).collect(Collectors.toList()));
        return new ArrayList<>(result);
    }

    private void notification(String msg) {
        final Notification notification = NOTIFICATION_GROUP.createNotification(msg, NotificationType.INFORMATION);
        notification.notify(this.project);
    }

    private List<RequestMappingInvokeBean> controllerFilter(List<RequestMappingInvokeBean> source, String searchText) {
        Set<RequestMappingInvokeBean> result = new HashSet<>();
        result.addAll(source.stream().filter(requestMappingInvokeBean -> requestMappingInvokeBean.getUrl().contains(searchText)).collect(Collectors.toList()));
        result.addAll(source.stream().filter(requestMappingInvokeBean -> requestMappingInvokeBean.getMethodName().contains(searchText)).collect(Collectors.toList()));
        result.addAll(source.stream().filter(requestMappingInvokeBean -> requestMappingInvokeBean.getSimpleClassName().contains(searchText)).collect(Collectors.toList()));
        return new ArrayList<>(result);
    }

    private void setController(List<RequestMappingInvokeBean> controller) {
        requestMappingFilterResult = controller;
        DefaultListModel<String[]> listModel = new DefaultListModel<>();
        for (RequestMappingInvokeBean requestMappingInvokeBean : controller) {
            listModel.addElement(new String[]{requestMappingInvokeBean.getUrl(), requestMappingInvokeBean.getMethodName(), requestMappingInvokeBean.getSimpleClassName()});
        }
        controllerJList.setModel(listModel);
    }

    private void setScheduleData(List<ScheduledInvokeBean> scheduledInvokeBeans) {
        scheduledFilterResult = scheduledInvokeBeans;
        DefaultListModel<ScheduledInvokeBean> defaultListModel = new DefaultListModel<>();
        defaultListModel.addAll(scheduledInvokeBeans);
        scheduleJList.setModel(defaultListModel);

    }

    @Override
    public void pluginMessage(String msg) {
        try {
            ProjectRequestBean requestBean = new Gson().fromJson(msg, ProjectRequestBean.class);
            if (BEAN_INFO.equalsIgnoreCase(requestBean.getType())) {
                this.projectRequestBean = requestBean;
                setController(controllerFilter(projectRequestBean.getController(), controllerSearchTextField.getText()));
                setScheduleData(scheduledFilter(projectRequestBean.getScheduled(), scheduledSearchTextField.getText()));
                return;
            }
            if (RESPONSE_INFO.equalsIgnoreCase(requestBean.getType()) && requestBean.isJson()) {
                if (requestBean.getResponse() == null) return;
                SwingUtilities.invokeLater(() -> {
                    JsonBrowse jsonBrowse = new JsonBrowse(requestBean.getResponse());
                    jsonBrowse.show();

                });
            }
        } catch (Exception e) {
            notification(e.getMessage());
        }

    }

    public JComponent getView() {
        JPanel scheduleJPanel = new JPanel();
        scheduleJPanel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(scheduleJList);
        scrollPane.setBorder(JBUI.Borders.empty());
        scheduleJPanel.add(scrollPane, BorderLayout.CENTER);
        scheduleJPanel.add(scheduledSearchTextField, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());

        JScrollPane jScrollPane = new JScrollPane(controllerJList);
        jScrollPane.setBorder(JBUI.Borders.empty());
        scrollPane.setBorder(JBUI.Borders.empty());
        contentPanel.add(jScrollPane, BorderLayout.CENTER);
        contentPanel.add(controllerSearchTextField, BorderLayout.NORTH);
        contentPanel.setBorder(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab(ResourceBundleUtils.getString("timer"), scheduleJPanel);
        tabbedPane.addTab(ResourceBundleUtils.getString("controller"), contentPanel);

        return tabbedPane;
    }
}
