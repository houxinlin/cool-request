package com.cool.request.view.component;

import com.cool.request.action.actions.BaseAnAction;
import com.cool.request.action.actions.ImportCurlParamAnAction;
import com.cool.request.action.actions.RequestEnvironmentAnAction;
import com.cool.request.action.actions.SaveCustomControllerAnAction;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.controller.CustomController;
import com.cool.request.common.bean.components.controller.DynamicController;
import com.cool.request.common.bean.components.controller.StaticController;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.common.listener.CommunicationListener;
import com.cool.request.utils.MessagesWrapperUtils;
import com.cool.request.utils.NavigationUtils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.ToolComponentPage;
import com.cool.request.view.View;
import com.cool.request.view.ViewRegister;
import com.cool.request.view.main.MainBottomHTTPInvokeViewPanel;
import com.cool.request.view.main.MainBottomHTTPResponseView;
import com.cool.request.view.main.MainTopTreeView;
import com.cool.request.view.tool.Provider;
import com.cool.request.view.tool.ProviderManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBSplitter;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class MainBottomHTTPContainer extends SimpleToolWindowPanel implements
        CommunicationListener,
        ToolComponentPage,
        Provider,
        View {
    public static final String PAGE_NAME = "HTTP";
    public static final String VIEW_ID = "@MainBottomHTTPContainer";
    private final MainBottomHTTPInvokeViewPanel mainBottomHttpInvokeViewPanel;
    private final MainBottomHTTPResponseView mainBottomHTTPResponseView;
    private final Project project;
    private final NavigationAnAction navigationAnAction;
    private final DefaultActionGroup menuGroup = new DefaultActionGroup();
    private boolean navigationVisible = false;


    public MainBottomHTTPContainer(Project project) {
        super(true);
        this.project = project;
        this.mainBottomHttpInvokeViewPanel = new MainBottomHTTPInvokeViewPanel(project);
        this.mainBottomHTTPResponseView = new MainBottomHTTPResponseView(project);

        ProviderManager.registerProvider(MainBottomHTTPContainer.class, CoolRequestConfigConstant.MainBottomHTTPContainerKey, this, project);

        ProviderManager.getProvider(ViewRegister.class, project).registerView(this);
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
        connection.subscribe(CoolRequestIdeaTopic.CLEAR_REQUEST_CACHE, (CoolRequestIdeaTopic.ClearRequestCacheEventListener) id -> {

        });
        connection.subscribe(CoolRequestIdeaTopic.CONTROLLER_CHOOSE_EVENT, (CoolRequestIdeaTopic.ControllerChooseEventListener) controller -> {
            if (controller instanceof CustomController) {
                if (navigationVisible) {
                    menuGroup.remove(navigationAnAction);
                    navigationVisible = false;
                }
            } else {
                if (!navigationVisible) {
                    if (controller instanceof StaticController || controller instanceof DynamicController) {
                        menuGroup.add(navigationAnAction, Constraints.LAST);
                        navigationVisible = true;
                    }
                }
            }

        });
        menuGroup.add(new RequestEnvironmentAnAction(project));
        menuGroup.addSeparator();

        menuGroup.add(new ImportCurlParamAnAction(project));
        menuGroup.add(new SaveCustomControllerAnAction(project));

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("bar", menuGroup, false);
        toolbar.setTargetComponent(this);

        setToolbar(toolbar.getComponent());

    }

    @Override
    public void setAttachData(Object object) {
        if (object == null) return;
        if (object instanceof MainTopTreeView.RequestMappingNode) {
            project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.CONTROLLER_CHOOSE_EVENT)
                    .onChooseEvent(((MainTopTreeView.RequestMappingNode) object).getData());
            return;
        }

        if (object instanceof MainTopTreeView.ScheduledMethodNode) {
            project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.SCHEDULED_CHOOSE_EVENT)
                    .onChooseEvent(((MainTopTreeView.ScheduledMethodNode) object).getData());
        }
    }

    @Override
    public String getPageId() {
        return PAGE_NAME;
    }

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
