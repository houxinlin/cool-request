package com.hxl.plugin.springboot.invoke.view;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.hxl.plugin.springboot.invoke.bean.InvokeBean;
import com.hxl.plugin.springboot.invoke.bean.ProjectRequestBean;
import com.hxl.plugin.springboot.invoke.bean.RequestMappingInvokeBean;
import com.hxl.plugin.springboot.invoke.bean.ScheduledInvokeBean;
import com.hxl.plugin.springboot.invoke.invoke.InvokeResult;
import com.hxl.plugin.springboot.invoke.invoke.ScheduledInvoke;
import com.hxl.plugin.springboot.invoke.net.PluginCommunication;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.utils.TextFieldTextChangedListener;
import com.hxl.plugin.springboot.invoke.view.browse.JsonBrowse;
import com.hxl.plugin.springboot.invoke.view.browse.TextBrowse;
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
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainView implements PluginCommunication.MessageCallback {
    /**
     * 项目模块中的Bean信息
     */
    class ProjectModuleBean {
        private Set<RequestMappingInvokeBean> controller = new HashSet<>();
        private Set<ScheduledInvokeBean> scheduled = new HashSet<>();
        public Set<RequestMappingInvokeBean> getController() {
            return controller;
        }

        public void setController(Set<RequestMappingInvokeBean> controller) {
            this.controller = controller;
        }

        public Set<ScheduledInvokeBean> getScheduled() {
            return scheduled;
        }

        public void setScheduled(Set<ScheduledInvokeBean> scheduled) {
            this.scheduled = scheduled;
        }
    }

    private final PlaceholderTextField controllerSearchTextField = new PlaceholderTextField();
    private final PlaceholderTextField scheduledSearchTextField = new PlaceholderTextField();
    private final NotificationGroup NOTIFICATION_GROUP = new NotificationGroup("com.hxl.plugin.scheduled-invoke", NotificationDisplayType.BALLOON, true);
    private final JList<String[]> controllerJList = new JList<>();

    /**
     * 项目中可能有多个模块，并且可以独立启动，每个模块有自己的通信断开
     * key:模块的通信端口
     * value:此模块下的所有Bean
     */
    private final Map<Integer, ProjectModuleBean> projectRequestBeanMap = new HashMap<>();
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
                        RequestMappingInvokeBean requestMappingInvokeBean = requestMappingFilterResult.get(index);
                        if (requestMappingInvokeBean==null) return;
                        int port = findPort(requestMappingInvokeBean);
                        if (port<=-1) {
                            notification("err:not found port");
                            return;
                        }
                        new InvokeDialog(requestMappingInvokeBean, port, invokeResult -> notification(invokeResult.getMessage())).show();
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
                        ScheduledInvokeBean scheduledInvokeBean = scheduledFilterResult.get(index);
                        if (scheduledInvokeBean ==null) return;
                        int port = findPort(scheduledInvokeBean);
                        if (port<=-1) {
                            notification("err:not found port");
                            return;
                        }
                        ScheduledInvoke.InvokeData invokeData = new ScheduledInvoke.InvokeData(scheduledInvokeBean.getId());
                        InvokeResult invoke = new ScheduledInvoke(port).invoke(invokeData);
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
                setController(controllerFilter(getAllRequstMapping(), text));
            }
        });
        scheduledSearchTextField.getDocument().addDocumentListener(new TextFieldTextChangedListener() {
            @Override
            public void textChanged() {
                String text = scheduledSearchTextField.getText();
                setScheduleData(scheduledFilter(getAllScheduled(), text));
            }
        });
    }
    private <T extends InvokeBean> int findPort(T invokeBean) {
        for (Integer port : projectRequestBeanMap.keySet()) {
            Set<? extends InvokeBean> invokeBeans = new HashSet<>();
            if (invokeBean instanceof RequestMappingInvokeBean) {
                invokeBeans = projectRequestBeanMap.get(port).getController();
            } else if (invokeBean instanceof ScheduledInvokeBean) {
                invokeBeans = projectRequestBeanMap.get(port).getScheduled();
            }
            for (InvokeBean mappingInvokeBean : invokeBeans) {
                if (mappingInvokeBean.getId().equals(invokeBean.getId())) {
                    return port;
                }
            }
        }
        return -1;
    }

    private List<ScheduledInvokeBean> scheduledFilter(Set<ScheduledInvokeBean> source, String searchText) {
        Set<ScheduledInvokeBean> result = new HashSet<>();
        result.addAll(source.stream().filter(scheduledInvokeBean -> scheduledInvokeBean.getClassName().contains(searchText)).collect(Collectors.toList()));
        result.addAll(source.stream().filter(scheduledInvokeBean -> scheduledInvokeBean.getMethodName().contains(searchText)).collect(Collectors.toList()));
        return new ArrayList<>(result);
    }

    private void notification(String msg) {
        final Notification notification = NOTIFICATION_GROUP.createNotification(msg, NotificationType.INFORMATION);
        notification.notify(this.project);
    }

    private List<RequestMappingInvokeBean> controllerFilter(Set<RequestMappingInvokeBean> source, String searchText) {
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

    private Set<RequestMappingInvokeBean> getAllRequstMapping() {
        Set<RequestMappingInvokeBean> requestMappingInvokeBeans = new HashSet<>();
        projectRequestBeanMap.values().forEach(ProjectModuleBean1 -> requestMappingInvokeBeans.addAll(ProjectModuleBean1.getController()));
        return requestMappingInvokeBeans;
    }

    private Set<ScheduledInvokeBean> getAllScheduled() {
        Set<ScheduledInvokeBean> scheduledInvokeBeans = new HashSet<>();
        projectRequestBeanMap.values().forEach(ProjectModuleBean1 -> scheduledInvokeBeans.addAll(ProjectModuleBean1.getScheduled()));
        return scheduledInvokeBeans;
    }
    private void removeIfClosePort(){
        Set<Integer> result  =new HashSet<>();
        for (Integer port : projectRequestBeanMap.keySet()) {
            try(SocketChannel ignored = SocketChannel.open(new InetSocketAddress(port))){
            }catch (Exception e){
                result.add(port);
            }
        }
        result.forEach(projectRequestBeanMap::remove);
    }

    @Override
    public void pluginMessage(String msg) {
        try {
            removeIfClosePort();
            ProjectRequestBean requestBean = new ObjectMapper().readValue(msg, ProjectRequestBean.class);
            if (BEAN_INFO.equalsIgnoreCase(requestBean.getType())) {
                //可能发生一个项目下多个模块共同推送
                ProjectModuleBean projectModuleBean = projectRequestBeanMap.computeIfAbsent(requestBean.getPort(), integer -> new ProjectModuleBean());
                if (requestBean.getScheduled() != null) {
                    projectModuleBean.getScheduled().addAll(requestBean.getScheduled());
                }
                if (requestBean.getController() != null) {
                    projectModuleBean.getController().addAll(requestBean.getController());
                }
                setController(controllerFilter(getAllRequstMapping(), controllerSearchTextField.getText()));
                setScheduleData(scheduledFilter(getAllScheduled(), scheduledSearchTextField.getText()));
                return;
            }
            if (RESPONSE_INFO.equalsIgnoreCase(requestBean.getType()) && requestBean.isJson()) {
                if (requestBean.getResponse() == null) return;
                SwingUtilities.invokeLater(() -> {
                    new JsonBrowse(requestBean.getResponse()).show();
                });
                return;
            }
            if (!requestBean.isJson() && requestBean.getResponse() != null) {
                SwingUtilities.invokeLater(() -> {
                    new TextBrowse(requestBean.getResponse()).show();
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
