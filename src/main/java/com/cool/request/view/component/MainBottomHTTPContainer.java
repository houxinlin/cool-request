package com.cool.request.view.component;

import com.cool.request.action.actions.BaseAnAction;
import com.cool.request.action.actions.CurlParamAnAction;
import com.cool.request.action.actions.RequestEnvironmentAnAction;
import com.cool.request.action.actions.SaveCustomControllerAnAction;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.controller.CustomController;
import com.cool.request.common.bean.components.controller.DynamicController;
import com.cool.request.common.bean.components.controller.StaticController;
import com.cool.request.common.bean.components.scheduled.BasicScheduled;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.common.listener.CommunicationListener;
import com.cool.request.utils.MessagesWrapperUtils;
import com.cool.request.utils.NavigationUtils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.ToolComponentPage;
import com.cool.request.view.View;
import com.cool.request.view.ViewRegister;
import com.cool.request.view.main.HTTPSendEventManager;
import com.cool.request.view.main.MainBottomHTTPInvokeViewPanel;
import com.cool.request.view.main.MainBottomHTTPResponseView;
import com.cool.request.view.main.MainTopTreeView;
import com.cool.request.view.tool.Provider;
import com.cool.request.view.tool.ProviderManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.JBSplitter;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class MainBottomHTTPContainer extends SimpleToolWindowPanel implements
        CommunicationListener,
        ToolComponentPage,
        Provider,
        View, Disposable {
    public static final String PAGE_NAME = "HTTP";
    public static final String VIEW_ID = "@MainBottomHTTPContainer";
    private MainBottomHTTPInvokeViewPanel mainBottomHttpInvokeViewPanel;
    private MainBottomHTTPResponseView mainBottomHTTPResponseView;
    private Project project;
    private NavigationAnAction navigationAnAction;
    private final DefaultActionGroup menuGroup = new DefaultActionGroup();
    private boolean navigationVisible = false;
    private HTTPSendEventManager sendEventManager = new HTTPSendEventManager();
    private Disposable disposable;

    public MainBottomHTTPContainer(Project project, Controller controller, Disposable disposable) {
        this(project, disposable);
        mainBottomHttpInvokeViewPanel.controllerChoose(controller);
        mainBottomHTTPResponseView.setController(controller);
    }

    public Controller getAttachController() {
        return mainBottomHttpInvokeViewPanel.getController();
    }

    @Override
    public void dispose() {
        Disposer.dispose(disposable);
    }

    public MainBottomHTTPContainer(Project project, Disposable disposable) {
        super(true);
        this.disposable = disposable;
        this.project = project;
        this.mainBottomHttpInvokeViewPanel = new MainBottomHTTPInvokeViewPanel(project, sendEventManager, this);
        this.mainBottomHTTPResponseView = new MainBottomHTTPResponseView(project);

        ProviderManager.getProvider(ViewRegister.class, project).registerView(mainBottomHTTPResponseView);

        JBSplitter jbSplitter = new JBSplitter(true, "", 0.5f);
        jbSplitter.setFirstComponent(this.mainBottomHttpInvokeViewPanel);
        jbSplitter.setSecondComponent(mainBottomHTTPResponseView);
        this.setLayout(new BorderLayout());
        this.setContent(jbSplitter);
        this.navigationAnAction = new NavigationAnAction(project);

        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(CoolRequestIdeaTopic.DELETE_ALL_DATA, (CoolRequestIdeaTopic.DeleteAllDataEventListener) () -> {
            mainBottomHttpInvokeViewPanel.clearRequestParam();
            mainBottomHTTPResponseView.setController(null);
        });

        /**
         * 订阅组件选中事件
         */
        connection.subscribe(CoolRequestIdeaTopic.COMPONENT_CHOOSE_EVENT, (CoolRequestIdeaTopic.ComponentChooseEventListener) component -> {
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
            }
        });
        menuGroup.add(new RequestEnvironmentAnAction(project));
        menuGroup.addSeparator();

        menuGroup.add(new CurlParamAnAction(project, this));
        menuGroup.add(new SaveCustomControllerAnAction(project));

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("bar", menuGroup, false);
        toolbar.setTargetComponent(this);

        setToolbar(toolbar.getComponent());

    }

    public MainBottomHTTPResponseView getMainBottomHTTPResponseView() {
        return mainBottomHTTPResponseView;
    }

    public MainBottomHTTPInvokeViewPanel getMainBottomHttpInvokeViewPanel() {
        return mainBottomHttpInvokeViewPanel;
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
            Object data = ((MainTopTreeView.BasicScheduledMethodNode) object).getData();
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
    class NavigationAnAction extends BaseAnAction {
        public NavigationAnAction(Project project) {
            super(project, () -> "Go To", CoolRequestIcons.NAVIGATION);
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
}
