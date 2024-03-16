package com.cool.request.view.component;

import com.cool.request.action.actions.*;
import com.cool.request.common.bean.EmptyEnvironment;
import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.controller.CustomController;
import com.cool.request.common.bean.components.controller.DynamicController;
import com.cool.request.common.bean.components.controller.StaticController;
import com.cool.request.common.bean.components.scheduled.BasicScheduled;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.cool.request.common.listener.CommunicationListener;
import com.cool.request.common.state.CoolRequestEnvironmentPersistentComponent;
import com.cool.request.utils.MessagesWrapperUtils;
import com.cool.request.utils.NavigationUtils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.ToolComponentPage;
import com.cool.request.view.View;
import com.cool.request.view.main.HTTPEventManager;
import com.cool.request.view.main.MainBottomHTTPInvokeViewPanel;
import com.cool.request.view.main.MainBottomHTTPResponseView;
import com.cool.request.view.main.MainTopTreeView;
import com.cool.request.view.tool.Provider;
import com.cool.request.view.widget.FilterTextView;
import com.intellij.ide.DataManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.JBSplitter;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class MainBottomHTTPContainer extends SimpleToolWindowPanel implements
        CommunicationListener,
        ToolComponentPage,
        Provider,
        View, Disposable, FilterTextView.ClickListener {
    public static final String PAGE_NAME = "HTTP";
    public static final String VIEW_ID = "@MainBottomHTTPContainer";
    private final MainBottomHTTPInvokeViewPanel mainBottomHttpInvokeViewPanel;
    private final MainBottomHTTPResponseView mainBottomHTTPResponseView;
    private final Project project;
    private final NavigationAnAction navigationAnAction;
    private final DefaultActionGroup menuGroup = new DefaultActionGroup();
    private boolean navigationVisible = false;
    private Controller controller;
    private final FilterTextView environment = new FilterTextView("Environment", this);

    public MainBottomHTTPContainer(Project project, Controller controller) {
        this(project);
        this.controller = controller;
        mainBottomHttpInvokeViewPanel.controllerChoose(controller);
        mainBottomHTTPResponseView.setController(controller);
        mainBottomHTTPResponseView.controllerChoose((controller));
    }

    public MainBottomHTTPContainer(Project project) {
        super(true);
        this.project = project;
        this.mainBottomHTTPResponseView = new MainBottomHTTPResponseView(project);
        HTTPEventManager sendEventManager = new HTTPEventManager();
        this.mainBottomHttpInvokeViewPanel = new MainBottomHTTPInvokeViewPanel(
                project,
                sendEventManager,
                this);
        sendEventManager.register(mainBottomHTTPResponseView);
        Disposer.register(this, mainBottomHttpInvokeViewPanel);
        Disposer.register(this, mainBottomHTTPResponseView);
        JBSplitter jbSplitter = new JBSplitter(true, "", 0.5f);
        jbSplitter.setFirstComponent(this.mainBottomHttpInvokeViewPanel);
        jbSplitter.setSecondComponent(mainBottomHTTPResponseView);
        this.setLayout(new BorderLayout());
        this.setContent(jbSplitter);
        this.navigationAnAction = new NavigationAnAction(project);

        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(CoolRequestIdeaTopic.DELETE_ALL_DATA, () -> {
            mainBottomHttpInvokeViewPanel.clearRequestParam();
            mainBottomHTTPResponseView.setController(null);
        });
        Disposer.register(this, connection);

        if (!(this instanceof TabMainBottomHTTPContainer)) {
            connection.subscribe(CoolRequestIdeaTopic.COMPONENT_CHOOSE_EVENT, component -> {
                if (component instanceof CustomController) {
                    if (navigationVisible) {
                        menuGroup.remove(navigationAnAction);
                        navigationVisible = false;
                    }
                } else {
                    if (!navigationVisible) {
                        if (component instanceof StaticController || component instanceof DynamicController) {
                            menuGroup.add(navigationAnAction, Constraints.LAST);
                            navigationVisible = true;
                        }
                    }
                }
                if (component instanceof BasicScheduled) {
                    mainBottomHttpInvokeViewPanel.scheduledChoose(((BasicScheduled) component));
                }
                if (component instanceof Controller) {
                    mainBottomHttpInvokeViewPanel.controllerChoose(((Controller) component));
                    mainBottomHTTPResponseView.controllerChoose(((Controller) component));
                }
            });
        }

        menuGroup.add(new CurlParamAnAction(project, this));
        menuGroup.add(new SaveCustomControllerAnAction(project, this));
        if (this instanceof TabMainBottomHTTPContainer && !(controller instanceof CustomController)) {
            menuGroup.add(navigationAnAction, Constraints.LAST);
        }
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("bar", menuGroup, true);
        toolbar.setTargetComponent(this);

        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        jPanel.add(environment);
        jPanel.add(toolbar.getComponent());
        setToolbar(jPanel);
        environment.setContent(getSelectEnvironmentName());

    }

    public MainBottomHTTPResponseView getMainBottomHTTPResponseView() {
        return mainBottomHTTPResponseView;
    }

    public MainBottomHTTPInvokeViewPanel getMainBottomHttpInvokeViewPanel() {
        return mainBottomHttpInvokeViewPanel;
    }

    public Controller getAttachController() {
        return mainBottomHttpInvokeViewPanel.getController();
    }

    @Override
    public void dispose() {
    }

    private String getSelectEnvironmentName() {
        CoolRequestEnvironmentPersistentComponent.State coolRequestEnvironmentPersistentComponentState
                = CoolRequestEnvironmentPersistentComponent.getInstance(project);

        for (RequestEnvironment environment : coolRequestEnvironmentPersistentComponentState.getEnvironments()) {
            boolean isSelect = StringUtils.isEqualsIgnoreCase(coolRequestEnvironmentPersistentComponentState.getSelectId(), environment.getId());
            if (isSelect) return environment.getEnvironmentName();
        }
        return "None";
    }

    @Override
    public void onClick(Component component) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new EnvironmentSettingAnAction(project));
        group.addSeparator();
        //加载用户配置的环境
        CoolRequestEnvironmentPersistentComponent.State coolRequestEnvironmentPersistentComponentState
                = CoolRequestEnvironmentPersistentComponent.getInstance(project);
        for (RequestEnvironment environment : coolRequestEnvironmentPersistentComponentState.getEnvironments()) {
            boolean isSelect = StringUtils.isEqualsIgnoreCase(coolRequestEnvironmentPersistentComponentState.getSelectId(), environment.getId());
            group.add(new EnvironmentItemAnAction(project, environment, isSelect ? CoolRequestIcons.GREEN : null));
        }
        //添加一个空环境
        EmptyEnvironment emptyEnvironment = new EmptyEnvironment();
        boolean isSelect = StringUtils.isEqualsIgnoreCase(coolRequestEnvironmentPersistentComponentState.getSelectId(), emptyEnvironment.getId());
        group.add(new EnvironmentItemAnAction(project, emptyEnvironment, isSelect ? CoolRequestIcons.GREEN : null));


        DataContext dataContext = DataManager.getInstance().getDataContext(component);
        JBPopupFactory.getInstance().createActionGroupPopup(
                        null, group, dataContext, JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                        false, null, 10, null, "popup@RequestEnvironmentAnAction")
                .showUnderneathOf(component);
    }

    /**
     * 其他页面对此页面主动跳转时候的附加数据
     *
     * @param object 附加数据
     */
    @Override
    public void setAttachData(Object object) {
        if (object == null) return;
        if (object instanceof MainTopTreeView.RequestMappingNode) {
            project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.COMPONENT_CHOOSE_EVENT)
                    .onChooseEvent(((MainTopTreeView.RequestMappingNode) object).getData());
            return;
        }

        if (object instanceof MainTopTreeView.BasicScheduledMethodNode) {
            Object data = ((MainTopTreeView.BasicScheduledMethodNode<?>) object).getData();
            project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.COMPONENT_CHOOSE_EVENT)
                    .onChooseEvent(((BasicScheduled) data));
        }

    }

    @Override
    public String getPageId() {
        return PAGE_NAME;
    }

    /**
     * 代码导航
     */
    class NavigationAnAction extends DynamicAnAction {
        public NavigationAnAction(Project project) {
            super(project, () -> "Go To", KotlinCoolRequestIcons.INSTANCE.getNAVIGATION());
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            Controller controller = mainBottomHttpInvokeViewPanel.getController();
            if (controller instanceof CustomController) {
                MessagesWrapperUtils.showErrorDialog(ResourceBundleUtils.getString("custom.api.unable.locate"), "Tip");
                return;
            }
            if (controller == null) return;
            NavigationUtils.jumpToControllerMethod(project, controller);
        }
    }

    @Override
    public String getViewId() {
        return VIEW_ID;
    }

    public class EnvironmentItemAnAction extends BaseAnAction {
        private RequestEnvironment requestEnvironment;

        public EnvironmentItemAnAction(Project project, RequestEnvironment requestEnvironment, Icon icon) {
            super(project, () -> requestEnvironment.getEnvironmentName(), icon);
            this.requestEnvironment = requestEnvironment;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            //设置环境
            environment.setContent(requestEnvironment.getEnvironmentName());
            CoolRequestEnvironmentPersistentComponent.getInstance(getProject()).setSelectId(requestEnvironment.getId());
            getProject().getMessageBus().syncPublisher(CoolRequestIdeaTopic.ENVIRONMENT_CHANGE).event();
        }
    }
}
