/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * HttpRequestParamPanel.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.view.main;

import com.cool.request.common.bean.BeanInvokeSetting;
import com.cool.request.common.bean.EmptyEnvironment;
import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.cache.CacheStorageService;
import com.cool.request.common.cache.ComponentCacheManager;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.cool.request.common.model.ProjectStartupModel;
import com.cool.request.common.state.CustomControllerFolderPersistent;
import com.cool.request.components.CoolRequestPluginDisposable;
import com.cool.request.components.http.*;
import com.cool.request.components.http.net.*;
import com.cool.request.components.http.net.request.StandardHttpRequestParam;
import com.cool.request.lib.curl.CurlImporter;
import com.cool.request.lib.springmvc.HttpRequestInfo;
import com.cool.request.lib.springmvc.JSONObjectGuessBody;
import com.cool.request.lib.springmvc.RequestCache;
import com.cool.request.lib.springmvc.StringBody;
import com.cool.request.scan.HttpRequestParamUtils;
import com.cool.request.utils.*;
import com.cool.request.view.ReflexSettingUIPanel;
import com.cool.request.view.component.MainBottomHTTPContainer;
import com.cool.request.view.dialog.CustomControllerFolderSelectDialog;
import com.cool.request.view.page.*;
import com.cool.request.view.table.RowDataState;
import com.cool.request.view.table.TablePanel;
import com.cool.request.view.tool.UserProjectManager;
import com.cool.request.view.tool.provider.RequestEnvironmentProvideImpl;
import com.cool.request.view.widget.SendButton;
import com.cool.request.view.widget.UrlEditorTextField;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class HttpRequestParamPanel extends JPanel
        implements
        IRequestParamManager,
        HTTPParamApply,
        ActionListener,
        Disposable {
    private final JPanel modelSelectPanel = new JPanel(new BorderLayout());
    private final ComboBox<String> httpInvokeModelComboBox = new ComboBox<>(new String[]{"http", "reflex"});
    private final List<RequestParamApply> requestParamApply = new ArrayList<>();
    private final HttpMethodComboBox requestMethodComboBox = new HttpMethodComboBox();
    private final EditorTextField requestUrlTextField;
    private final JPanel httpParamInputPanel = new JPanel();
    private final List<TablePanel> tablePanels = new ArrayList<>();
    private final Project project;
    private final RequestHeaderPage requestHeaderPage;
    private SendButton sendRequestButton = null;
    private final UrlPanelParamPage urlParamPage;
    private final UrlPathParamPage urlPathParamPage;
    private JBTabs httpParamTab;
    private RequestBodyPage requestBodyPage;
    private TabInfo reflexInvokePanelTabInfo;
    private Controller controller;
    private final MainBottomRequestContainer mainBottomRequestContainer;
    private ScriptPage scriptPage;
    private TabInfo headTab;
    private TabInfo urlParamPageTabInfo;
    private TabInfo urlPathParamPageTabInfo;
    private TabInfo requestBodyTabInfo;
    private TabInfo scriptTabInfo;
    private ReflexSettingUIPanel reflexSettingUIPanel;
    private ActionListener sendActionListener;
    private final MainBottomHTTPContainer mainBottomHTTPContainer;

    public HttpRequestParamPanel(Project project,
                                 MainBottomRequestContainer mainBottomRequestContainer,
                                 MainBottomHTTPContainer mainBottomHTTPContainer) {
        this.project = project;
        this.mainBottomHTTPContainer = mainBottomHTTPContainer;
        this.mainBottomRequestContainer = mainBottomRequestContainer;
        this.requestHeaderPage = new RequestHeaderPage(project);
        this.urlParamPage = new UrlPanelParamPage(project);
        this.urlPathParamPage = new UrlPathParamPage(project);
        requestParamApply.add(createBasicRequestParamApply());
        requestUrlTextField = new UrlEditorTextField(project, getRequestParamManager());
        init();
        initEvent();
        loadText();
    }

    @Override
    public void dispose() {
        requestBodyPage.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        stopAllEditor();
        if (this.sendActionListener != null) sendActionListener.actionPerformed(e);
    }

    public void beginSend(RequestContext requestContext, ProgressIndicator progressIndicator) {
        if (StringUtils.isEqualsIgnoreCase(getCurrentController().getId(), requestContext.getController().getId())) {
            SwingUtilities.invokeLater(() -> sendRequestButton.setLoadingStatus(true));
        }
    }

    @Override
    public void endSend(RequestContext requestContext, HTTPResponseBody httpResponseBody, ProgressIndicator progressIndicator) {
        if (StringUtils.isEqualsIgnoreCase(getCurrentController().getId(), requestContext.getController().getId())) {
            SwingUtilities.invokeLater(() -> sendRequestButton.setLoadingStatus(false));
        }
    }

    @Override
    public void postApplyParam(StandardHttpRequestParam standardHttpRequestParam) {
        for (RequestParamApply request : requestParamApply) {
            request.configRequest(standardHttpRequestParam);
        }
    }

    /**
     * Load reflex panel if reflection is selected
     */
    private void loadReflexInvokePanel(boolean show) {
        if (show) {
            if (controller != null) {
                reflexSettingUIPanel.setRequestInfo(getRequestCacheOrCreate(controller));
            }
            httpParamTab.addTab(reflexInvokePanelTabInfo);
            return;
        }
        httpParamTab.removeTab(reflexInvokePanelTabInfo);
    }

    public void setSendRequestClickEvent(ActionListener actionListener) {
        sendActionListener = actionListener;
    }

    private void initEvent() {
        httpInvokeModelComboBox.addItemListener(e -> {
            Object item = e.getItem();
            loadReflexInvokePanel(!"HTTP".equalsIgnoreCase(item.toString()));
        });

        MessageBusConnection applicationMessageBus = ApplicationManager.getApplication().getMessageBus().connect();
        applicationMessageBus.subscribe(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE, this::loadText);

        Disposer.register(CoolRequestPluginDisposable.getInstance(project), applicationMessageBus);
        MessageBusConnection projectMessage = project.getMessageBus().connect();
        Disposer.register(CoolRequestPluginDisposable.getInstance(project), projectMessage);
        //检测到有响应结果，则改变button状态
        projectMessage.subscribe(CoolRequestIdeaTopic.HTTP_RESPONSE, (requestId, invokeResponseModel, requestContext) -> {
            Controller currentController = getCurrentController();
            if (currentController == null) return;

        });
        //检测到环境发生改变，则重置环境
        projectMessage.subscribe(CoolRequestIdeaTopic.ENVIRONMENT_CHANGE, () -> {
            if (controller != null) {
                runLoadControllerInfoOnMain(controller);
            }
        });
        projectMessage.subscribe(CoolRequestIdeaTopic.CLEAR_REQUEST_CACHE, new CoolRequestIdeaTopic.ClearRequestCacheEventListener() {
            @Override
            public void onClearAllEvent() {
                if (getCurrentController() == null) return;
                runLoadControllerInfoOnMain(getCurrentController());
            }

            @Override
            public void onClearEvent(List<String> ids) {
                if (getCurrentController() == null) return;
                if (ids.contains(getCurrentController().getId())) {
                    runLoadControllerInfoOnMain(getCurrentController());
                }
            }
        });
    }

    private void loadText() {
        headTab.setText(ResourceBundleUtils.getString("header"));
        urlParamPageTabInfo.setText(ResourceBundleUtils.getString("param"));
        requestBodyTabInfo.setText(ResourceBundleUtils.getString("body"));
        scriptTabInfo.setText(ResourceBundleUtils.getString("script"));
        reflexInvokePanelTabInfo.setText(ResourceBundleUtils.getString("invoke.setting"));

        sendRequestButton.getButtonPresentation().setIcon(KotlinCoolRequestIcons.INSTANCE.getSEND().invoke());
    }

    private RequestParamApply createBasicRequestParamApply() {
        return standardHttpRequestParam -> {
            if (controller != null) {
                standardHttpRequestParam.setId(controller.getId());
            }
            standardHttpRequestParam.setUrl(requestUrlTextField.getText());
            standardHttpRequestParam.setMethod(requestMethodComboBox.getHTTPMethod());
        };
    }


    private void init() {
        setLayout(new BorderLayout(0, 0));
        httpParamInputPanel.setLayout(new BorderLayout(0, 0));
        sendRequestButton = SendButton.newSendButton();
        modelSelectPanel.add(httpInvokeModelComboBox, BorderLayout.WEST);
        modelSelectPanel.add(requestMethodComboBox, BorderLayout.CENTER);
        requestUrlTextField.setText("");
        httpParamInputPanel.add(modelSelectPanel, BorderLayout.WEST);
        httpParamInputPanel.add(requestUrlTextField);
        Presentation presentation = new Presentation();
        presentation.setIcon(KotlinCoolRequestIcons.INSTANCE.getSEND().invoke());
        httpParamInputPanel.add(sendRequestButton, BorderLayout.EAST);
        add(httpParamInputPanel, BorderLayout.NORTH);


        httpParamTab = new JBTabsImpl(project);

        //request header input page
        requestParamApply.add(requestHeaderPage);
        headTab = new TabInfo(new JBScrollPane(requestHeaderPage));
        headTab.setText("Header");
        httpParamTab.addTab(headTab);

        //url param input page
        requestParamApply.add(urlParamPage);
        urlParamPageTabInfo = new TabInfo(new JBScrollPane(urlParamPage));
        urlParamPageTabInfo.setText("Param");
        httpParamTab.addTab(urlParamPageTabInfo);

        requestParamApply.add(urlPathParamPage);
        urlPathParamPageTabInfo = new TabInfo(new JBScrollPane(urlPathParamPage));
        urlPathParamPageTabInfo.setText("Path");
        httpParamTab.addTab(urlPathParamPageTabInfo);


        //request body input page
        requestBodyPage = new RequestBodyPage(project, this);
        requestParamApply.add(requestBodyPage);
        requestBodyTabInfo = new TabInfo(requestBodyPage);
        requestBodyTabInfo.setText("Body");
        httpParamTab.addTab(requestBodyTabInfo);

        //script input page
        scriptPage = new ScriptPage(project);
        scriptTabInfo = new TabInfo(scriptPage);
        scriptTabInfo.setText("Script");
        httpParamTab.addTab(scriptTabInfo);

        add(httpParamTab.getComponent(), BorderLayout.CENTER);

        reflexSettingUIPanel = new ReflexSettingUIPanel(project);
        reflexInvokePanelTabInfo = new TabInfo(reflexSettingUIPanel.getRoot());
        reflexInvokePanelTabInfo.setText("Invoke Setting");

        sendRequestButton.addActionListener(this);
        httpParamInputPanel.invalidate();
        SwingUtilities.invokeLater(() -> sendRequestButton.getButtonPresentation().setEnabledAndVisible(true));

        tablePanels.add(requestHeaderPage);
        tablePanels.add(urlParamPage);
        tablePanels.add(urlPathParamPage);
        tablePanels.add(requestBodyPage.getUrlencodedRequestBodyPage());
        tablePanels.add(requestBodyPage.getFormDataRequestBodyPage());
    }

    /**
     * clear all request param
     */
    public void clearAllRequestParam() {
        this.controller = null;
        requestBodyPage.setJsonBodyText("");
        requestBodyPage.setXmlBodyText("");
        requestBodyPage.setRawBodyText("");
        requestBodyPage.setBinaryRequestBodyFile(BinaryRequestBodyPage.DEFAULT_NAME);
        setUrl("");
        setFormData(null);
        setUrlencodedBody(null);
        setUrlParam(null);
        setHttpHeader(null);
    }

    public IRequestParamManager getRequestParamManager() {
        return this;
    }


    public void runLoadControllerInfoOnMain(Controller controller) {
        if (SwingUtilities.isEventDispatchThread()) {
            loadControllerInfo(controller);
        } else {
            SwingUtilities.invokeLater(() -> loadControllerInfo(controller));
        }
    }

    private String getBaseUrl(Controller controller) {
        if (controller instanceof CustomController) return controller.getUrl();
        UserProjectManager userProjectManager = UserProjectManager.getInstance(project);
        int port = controller.getServerPort();
        /**
         * 启用动态技术，如果当前只有一个应用启动，那么端口则优先使用推送的
         */
        Set<Integer> ports = userProjectManager
                .getSpringBootApplicationStartupModel()
                .stream()
                .map(ProjectStartupModel::getProjectPort)
                .collect(Collectors.toSet());
        if (ports.size() == 1) {
            port = new ArrayList<>(ports).get(0);
        }
        return "http://localhost:" + port;
    }

    private void loadControllerInfo(Controller controller) {
        if (!(controller instanceof TemporaryController)) {
            clearAllRequestParam();  //可以被清除所有数据，并重新加载
        }
        this.controller = controller;
        if (this.controller == null) return;
        //从缓存中获取按钮的状态，true表示可用，需要重置状态，因为有请求可能还处于发送状态
        boolean enabledSendButton = mainBottomRequestContainer.canEnabledSendButton(controller.getId());
        this.sendRequestButton.setLoadingStatus(!enabledSendButton);
        sendRequestButton.setEnabled(enabledSendButton);
        //临时请求不加载任任何信息
        if (controller instanceof TemporaryController) {
            return;
        }
        //从缓存中加载以前的设置
        RequestCache requestCache = ComponentCacheManager.getRequestParamCache(controller.getId());

        String url = fixFullUrl(controller, requestCache, getBaseUrl(controller));
        RequestEnvironment selectRequestEnvironment = RequestEnvironmentProvideImpl.getInstance(project).getSelectRequestEnvironment();

        if (!(controller instanceof CustomController) && !(selectRequestEnvironment instanceof EmptyEnvironment)) {
            url = StringUtils.joinUrlPath(selectRequestEnvironment.getHostAddress(),
                    UrlUtils.addUrlParam(controller.getUrl(), UrlUtils.getUrlParam(url)));
        }
        if (requestCache == null) requestCache = createDefaultRequestCache(controller);

        requestUrlTextField.setText(url);
        scriptPage.setLog(controller.getId(), requestCache.getScriptLog());

        IRequestParamManager requestParamManager = getRequestParamManager();
        requestParamManager.setInvokeHttpMethod(requestCache.getInvokeModelIndex());//调用方式
        //优先使用缓存中的
        String cacheHttpMethod = requestCache.getHttpMethod();
        requestParamManager.setHttpMethod(HttpMethod.parse(cacheHttpMethod != null ? cacheHttpMethod : controller.getHttpMethod()));//http方式
        requestParamManager.setHttpHeader(requestCache.getHeaders());
        requestParamManager.setUrlParam(requestCache.getUrlParams());
        requestParamManager.setPathParam(requestCache.getUrlPathParams());
        requestParamManager.setUrlencodedBody(requestCache.getUrlencodedBody());
        requestParamManager.setFormData(requestCache.getFormDataInfos());
        requestParamManager.setRequestBody(requestCache.getRequestBodyType(), requestCache.getRequestBody());
        requestParamManager.switchRequestBodyType(new MediaType(requestCache.getRequestBodyType()));

        scriptPage.setScriptText(requestCache.getRequestScript(), requestCache.getResponseScript());
        //是否显示反射设置面板
        Object selectedItem = httpInvokeModelComboBox.getSelectedItem();
        loadReflexInvokePanel(!"HTTP".equalsIgnoreCase(selectedItem == null ? "" : selectedItem.toString()));

    }

    private RequestCache getRequestCacheOrCreate(Controller controller) {
        RequestCache requestCache = ComponentCacheManager.getRequestParamCache(controller.getId());
        if (requestCache == null) return createDefaultRequestCache(controller);
        return requestCache;
    }

    /**
     * 修正url主机
     * 当某个API发起后，缓存数据，但是当下次主机发生改变后，重新恢复到最新得主机
     */
    @NotNull
    private String fixFullUrl(Controller controller, RequestCache requestCache, String base) {
        if (controller instanceof CustomController) return controller.getUrl();
        String url = requestCache != null ? requestCache.getUrl() : StringUtils.joinUrlPath(base, controller.getContextPath(), controller.getUrl());
        //如果有缓存，但是开头不是当前的主机、端口、和上下文,但是要保存请求参数
        if (requestCache != null && !url.startsWith(StringUtils.joinUrlPath(base, controller.getContextPath()))) {
            String query = "";
            try {
                query = new URL(url).getQuery();
            } catch (MalformedURLException ignored) {
            }
            if (query == null) query = "";
            url = StringUtils.joinUrlPath(base, controller.getContextPath(), controller.getUrl());
            if (StringUtils.hasText(query)) {
                url = url + "?" + query;
            }
        }
        return url;
    }

    private RequestCache createDefaultRequestCache(Controller controller) {
        HttpRequestInfo httpRequestInfo = HttpRequestParamUtils.getHttpRequestInfo(project, controller);
        String requestBodyText = "";
        if (httpRequestInfo.getRequestBody() instanceof JSONObjectGuessBody) {
            requestBodyText = GsonUtils.toJsonString(((JSONObjectGuessBody) httpRequestInfo.getRequestBody()).getJson());
        }
        if (httpRequestInfo.getRequestBody() instanceof StringBody) {
            requestBodyText = "";
        }

        return RequestCache.RequestCacheBuilder.aRequestCache()
                .withInvokeModelIndex(0)
                .withResponseScript("")
                .withRequestScript("")
                .withUseProxy(false)
                .withUseInterceptor(false)
                .withScriptLog("")
                .withUrlPathParams(httpRequestInfo.getPathParams().stream().map(requestParameterDescription ->
                        new KeyValue(requestParameterDescription.getName(), "")).collect(Collectors.toList()))
                .withHeaders(httpRequestInfo.getHeaders().stream().map(requestParameterDescription ->
                        new KeyValue(requestParameterDescription.getName(), "")).collect(Collectors.toList()))
                .withUrlParams(httpRequestInfo.getUrlParams().stream().map(requestParameterDescription ->
                        new KeyValue(requestParameterDescription.getName(), "")).collect(Collectors.toList()))
                .withRequestBodyType(httpRequestInfo.getContentType())
                .withRequestBody(requestBodyText)
                .withUrlencodedBody(httpRequestInfo.getUrlencodedBody().stream().map(requestParameterDescription ->
                        new KeyValue(requestParameterDescription.getName(), "")).collect(Collectors.toList()))
                .withFormDataInfos(httpRequestInfo.getFormDataInfos().stream().map(requestParameterDescription ->
                        new FormDataInfo(requestParameterDescription.getName(),
                                "", requestParameterDescription.getType())).collect(Collectors.toList()))
                .build();
    }

    @Override
    public RequestCache createRequestCache() {
        IRequestParamManager requestParamManager = this;
        BeanInvokeSetting beanInvokeSetting = requestParamManager.getBeanInvokeSetting();
        return RequestCache.RequestCacheBuilder.aRequestCache()
                .withHttpMethod(requestParamManager.getHttpMethod().toString())
                .withHeaders(requestParamManager.getHttpHeader(RowDataState.all))
                .withUrlParams(requestParamManager.getUrlParam(RowDataState.all))
                .withRequestBodyType(requestParamManager.getRequestBodyType().getValue())
                .withFormDataInfos(requestParamManager.getFormData(RowDataState.all))
                .withUrlencodedBody(requestParamManager.getUrlencodedBody(RowDataState.all))
                .withRequestBody(requestParamManager.getRequestBody())
                .withUrlPathParams(requestParamManager.getPathParam(RowDataState.all))
                .withUrl(requestParamManager.getUrl())
                .withPort(controller == null ? -1 : controller.getServerPort())
                .withScriptLog("")
                .withRequestScript(requestParamManager.getRequestScript())
                .withResponseScript(requestParamManager.getResponseScript())
                .withContentPath(controller == null ? "" : controller.getContextPath())
                .withUseProxy(beanInvokeSetting.isUseProxy())
                .withUseInterceptor(beanInvokeSetting.isUseInterceptor())
                .withInvokeModelIndex(requestParamManager.getInvokeModelIndex())
                .build();
    }

    @Override
    public void saveAsCustomController(AnActionEvent e) {
        String url = getUrl();
        if (!UrlUtils.isURL(url)) {
            MessagesWrapperUtils.showErrorDialog(ResourceBundleUtils.getString("invalid.url"), ResourceBundleUtils.getString("tip"));
            return;
        }
        if (getCurrentController() instanceof CustomController) {
            DefaultActionGroup defaultActionGroup = new DefaultActionGroup(
                    new SaveControllerWithNew(),
                    new SaveControllerWithOverride());

            JBPopupFactory.getInstance().createActionGroupPopup(
                            null, defaultActionGroup, e.getDataContext(), JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                            false, null, 10, null, "popup@RefreshAction")
                    .showUnderneathOf(e.getInputEvent().getComponent());
        } else {
            saveNewCustomApi();
        }

    }

    private void saveNewCustomApi() {
        //调用自定义目录选择对话框
        CustomControllerFolderSelectDialog customControllerFolderSelectDialog = new CustomControllerFolderSelectDialog(project);
        customControllerFolderSelectDialog.show();
        Object selectResult = customControllerFolderSelectDialog.getSelectResult();
        if (selectResult == null) return;
        if (selectResult instanceof CustomControllerFolderSelectDialog.FolderTreeNode) {
            CustomControllerFolderSelectDialog.FolderTreeNode folderTreeNode = (CustomControllerFolderSelectDialog.FolderTreeNode) selectResult;
            CustomControllerFolderPersistent.Folder folder = (CustomControllerFolderPersistent.Folder) folderTreeNode.getUserObject();
            CustomController customController = buildAsCustomController(CustomController.class);
            folder.getControllers().add(customController);
            saveAsCustomController(customController);
            //刷新自定义目录
            ApplicationManager.getApplication().getMessageBus().syncPublisher(CoolRequestIdeaTopic.REFRESH_CUSTOM_FOLDER).event();
            NotifyUtils.notification(project, "Save Success");
        }
    }

    private void saveAsCustomController(Controller controller) {
        //保存缓存
        CacheStorageService cacheStorageService = ApplicationManager.getApplication().getService(CacheStorageService.class);
        ComponentCacheManager.storageRequestCache(controller.getId(), createRequestCache());
        MainBottomHTTPResponseView mainBottomHTTPResponseView = mainBottomHTTPContainer.getMainBottomHTTPResponseView();
        if (mainBottomHTTPResponseView.getHttpResponseBody() != null) {
            cacheStorageService.storageResponseCache(controller.getId(), mainBottomHTTPResponseView.getHttpResponseBody());
        }

        this.controller = controller;
    }

    public <T extends Controller> T buildAsCustomController(Class<T> targetController) {
        T result = null;
        try {
            result = targetController.getConstructor().newInstance();
            result.setContextPath("");
            result.setHttpMethod(getHttpMethod().toString());
            result.setMethodName("");
            result.setSimpleClassName("");
            result.setUrl(getUrl());
            result.setId(UUID.randomUUID().toString());
        } catch (Exception ignored) {
        }
        return result;
    }

    @Override
    public ScriptLogPage getScriptLogPage() {
        return scriptPage.getScriptLogPage();
    }

    @Override
    public List<KeyValue> getPathParam(RowDataState rowDataState) {
        return urlPathParamPage.getTableMap(RowDataState.available);
    }

    @Override
    public void restParam() {
        setHttpHeader(null);
        setUrlParam(null);
        setFormData(null);
        setUrlencodedBody(null);
        setRequestBody("json", "");
    }

    @Override
    public void importCurl(String curl) {
        CurlImporter.doImport(curl, this);
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.parse(requestMethodComboBox.getSelectedItem());
    }

    @Override
    public String getRequestBody() {
        return requestBodyPage.getTextRequestBody();
    }

    @Override
    public boolean isAvailable() {
        return getCurrentController() != null;
    }

    @Override
    public int getInvokeModelIndex() {
        return httpInvokeModelComboBox.getSelectedIndex();
    }

    @Override
    public boolean isReflexRequest() {
        return getInvokeModelIndex() == 1;
    }

    @Override
    public Controller getCurrentController() {
        if (this.controller != null) {
            if (this.controller instanceof TemporaryController) {
                this.controller.setUrl(getUrl());
                this.controller.setHttpMethod(getHttpMethod().toString());
            }
            return this.controller;
        }
        return null;

    }

    @Override
    public String getContentTypeFromHeader() {
        for (KeyValue keyValue : getHttpHeader(RowDataState.available)) {
            if (StringUtils.isEqualsIgnoreCase(keyValue.getKey(), "content-type")) {
                return keyValue.getValue();
            }
        }
        return null;
    }

    @Override
    public BeanInvokeSetting getBeanInvokeSetting() {
        return reflexSettingUIPanel.getBeanInvokeSetting();
    }

    @Override
    public String getUrl() {
        return requestUrlTextField.getText();
    }

    @Override
    public int getInvokeHttpMethod() {
        return httpInvokeModelComboBox.getSelectedIndex();
    }

    @Override
    public List<KeyValue> getUrlencodedBody(RowDataState rowDataState) {
        return requestBodyPage.getUrlencodedBody();
    }

    @Override
    public List<KeyValue> getHttpHeader(RowDataState rowDataState) {
        return requestHeaderPage.getTableMap(RowDataState.available);
    }

    @Override
    public List<KeyValue> getUrlParam(RowDataState rowDataState) {
        return urlParamPage.getTableMap(RowDataState.available);
    }

    @Override
    public List<FormDataInfo> getFormData(RowDataState rowDataState) {
        return requestBodyPage.getFormDataInfo(RowDataState.available);
    }

    @Override
    public void setUrl(String url) {
        requestUrlTextField.setText(url);
    }

    @Override
    public void setHttpMethod(HttpMethod method) {
        for (int i = 0; i < requestMethodComboBox.getItemCount(); i++) {
            if (requestMethodComboBox.getItemAt(i).equals(method)) {
                requestMethodComboBox.setSelectedIndex(i);
            }
        }
    }

    @Override
    public void setInvokeHttpMethod(int index) {
        httpInvokeModelComboBox.setSelectedIndex(index);
    }

    @Override
    public void setHttpHeader(List<KeyValue> value) {
        requestHeaderPage.setTableData(Optional.ofNullable(value).orElse(new ArrayList<>()));
    }

    @Override
    public void setUrlParam(List<KeyValue> value) {
        urlParamPage.setTableData(Optional.ofNullable(value).orElse(new ArrayList<>()));
    }

    @Override
    public void setPathParam(List<KeyValue> value) {
        urlPathParamPage.setTableData(Optional.ofNullable(value).orElse(new ArrayList<>()));
    }

    @Override
    public void setFormData(List<FormDataInfo> value) {
        requestBodyPage.setFormData(Optional.ofNullable(value).orElse(new ArrayList<>()));
    }

    @Override
    public void setUrlencodedBody(List<KeyValue> value) {
        requestBodyPage.setUrlencodedBodyTableData(Optional.ofNullable(value).orElse(new ArrayList<>()));
    }

    /**
     * 这所虽然乱，但是没问题，需要优化，但是设计到的比较多
     */
    @Override
    public void setRequestBody(String mediaType, String body) {
        if (mediaType == null) return;
        if (StringUtils.isEmpty(body)) body = "";
        if (mediaType.contains("json")) {
            requestBodyPage.setJsonBodyText(GsonUtils.format(body));
            switchRequestBodyType(new MediaType(MediaTypes.APPLICATION_JSON));
            return;
        }
        if (mediaType.contains("xml")) {
            requestBodyPage.setXmlBodyText(body);
            switchRequestBodyType(new MediaType(MediaTypes.APPLICATION_XML));
            return;
        }
        if (mediaType.contains("binary")) {
            requestBodyPage.setBinaryRequestBodyFile(body);
            switchRequestBodyType(new MediaType(MediaTypes.APPLICATION_STREAM));
            return;
        }
        requestBodyPage.setRawBodyText(body);
        switchRequestBodyType(new MediaType(MediaTypes.TEXT));
    }

    @Override
    public MediaType getRequestBodyType() {
        return requestBodyPage.getSelectedRequestBodyMediaType();
    }

    @Override
    public void switchRequestBodyType(MediaType type) {
        requestBodyPage.switchRequestBodyType(type);
    }

    @Override
    public String getRequestScript() {
        return scriptPage.getRequestScriptText();
    }

    @Override
    public String getResponseScript() {
        return scriptPage.getResponseScriptText();
    }

    @Override
    public void stopAllEditor() {
        for (TablePanel tablePanel : tablePanels) {
            tablePanel.stopEditor();
        }
    }

    @Override
    public void switchToURlParamPage() {
        httpParamTab.select(urlParamPageTabInfo, true);
    }

    class SaveControllerWithNew extends AnAction {
        public SaveControllerWithNew() {
            super(ResourceBundleUtils.getString("custom.save.new"));
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            saveNewCustomApi();
        }
    }

    class SaveControllerWithOverride extends AnAction {
        public SaveControllerWithOverride() {
            super(ResourceBundleUtils.getString("custom.save.override"));
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            getCurrentController().setUrl(getUrl());
            saveAsCustomController(getCurrentController());
            NotifyUtils.notification(project, "Save Success");
        }
    }
}
