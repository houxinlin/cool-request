package com.cool.request.view.main;

import com.cool.request.common.bean.BeanInvokeSetting;
import com.cool.request.common.bean.EmptyEnvironment;
import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.bean.components.Component;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.controller.CustomController;
import com.cool.request.common.bean.components.controller.DynamicController;
import com.cool.request.common.bean.components.controller.TemporaryController;
import com.cool.request.common.cache.CacheStorageService;
import com.cool.request.common.cache.ComponentCacheManager;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.state.CustomControllerFolderPersistent;
import com.cool.request.component.ComponentType;
import com.cool.request.component.CoolRequestPluginDisposable;
import com.cool.request.component.http.net.*;
import com.cool.request.component.http.net.request.StandardHttpRequestParam;
import com.cool.request.lib.curl.CurlImporter;
import com.cool.request.lib.springmvc.*;
import com.cool.request.utils.*;
import com.cool.request.view.ReflexSettingUIPanel;
import com.cool.request.view.ViewRegister;
import com.cool.request.view.dialog.CustomControllerFolderSelectDialog;
import com.cool.request.view.page.*;
import com.cool.request.view.tool.ProviderManager;
import com.cool.request.view.widget.SendButton;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.JBTextField;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class HttpRequestParamPanel extends JPanel
        implements IRequestParamManager,
        HTTPParamApply, ActionListener {
    private final Project project;
    private final List<RequestParamApply> requestParamApply = new ArrayList<>();
    private final HttpMethodComboBox requestMethodComboBox = new HttpMethodComboBox();
    private final RequestHeaderPage requestHeaderPage;
    private final JTextField requestUrlTextField = new JBTextField();
    private final SendButton sendRequestButton = SendButton.newSendButton();
    private final JPanel modelSelectPanel = new JPanel(new BorderLayout());
    private final ComboBox<String> httpInvokeModelComboBox = new ComboBox<>(new String[]{"http", "reflex"});
    private final UrlPanelParamPage urlParamPage;
    private final UrlPathParamPage urlPathParamPage;
    private JBTabs httpParamTab;
    private RequestBodyPage requestBodyPage;
    private TabInfo reflexInvokePanelTabInfo;
    private Controller controller;
    private final MainBottomHTTPInvokeViewPanel mainBottomHTTPInvokeViewPanel;
    private ScriptPage scriptPage;
    private TabInfo headTab;
    private TabInfo urlParamPageTabInfo;
    private TabInfo urlPathParamPageTabInfo;
    private TabInfo requestBodyTabInfo;
    private TabInfo scriptTabInfo;
    private ReflexSettingUIPanel reflexSettingUIPanel;
    private ActionListener sendActionListener;

    public HttpRequestParamPanel(Project project,
                                 MainBottomHTTPInvokeViewPanel mainBottomHTTPInvokeViewPanel) {
        this.project = project;
        this.requestHeaderPage = new RequestHeaderPage(project);
        this.urlParamPage = new UrlPanelParamPage(project);
        this.urlPathParamPage = new UrlPathParamPage(project);
        this.mainBottomHTTPInvokeViewPanel = mainBottomHTTPInvokeViewPanel;
        ProviderManager.registerProvider(IRequestParamManager.class, CoolRequestConfigConstant.IRequestParamManagerKey, this, project);
        requestParamApply.add(createBasicRequestParamApply());
        init();
        initEvent();
        loadText();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        requestHeaderPage.stopEditor(); //请求头停止编辑
        urlParamPage.stopEditor(); //请求参数停止编辑
        requestBodyPage.getFormDataRequestBodyPage().stopEditor(); //form表单停止编辑
        requestBodyPage.getUrlencodedRequestBodyPage().stopEditor(); //form url停止编辑
        urlPathParamPage.stopEditor();              //path停止编辑

        if (this.sendActionListener != null) sendActionListener.actionPerformed(e);
    }

//    /**
//     * 应用全局参数时，全局参数会先被调用，但全局参数此时不知道被选中的是那个Post，由这里解决
//     */
//    @Override
//    public void preApplyParam(StandardHttpRequestParam standardHttpRequestParam) {
//        StandardHttpRequestParam cache = new StandardHttpRequestParam();
//        requestBodyPage.configRequest(cache);
//        /**
//         * 只支持form和form-url即可
//         */
//        if (cache.getBody() instanceof FormBody) {
//            standardHttpRequestParam.setBody(new FormBody(new ArrayList<>()));
//        }
//        if (cache.getBody() instanceof FormUrlBody) {
//            standardHttpRequestParam.setBody(new FormUrlBody(new ArrayList<>()));
//        }
//    }

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
        applicationMessageBus.subscribe(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE, (CoolRequestIdeaTopic.BaseListener) this::loadText);

        Disposer.register(CoolRequestPluginDisposable.getInstance(project), applicationMessageBus);
        MessageBusConnection projectMessage = project.getMessageBus().connect();

        //检测到有请求开始发送，则改变button状态
        projectMessage.subscribe(CoolRequestIdeaTopic.REQUEST_SEND_BEGIN, (CoolRequestIdeaTopic.ObjectListener) content -> {
            if (content instanceof Controller) {
                if (StringUtils.isEqualsIgnoreCase(getCurrentController().getId(), ((Controller) content).getId())) {
                    sendRequestButton.setLoadingStatus(true);
                }
            }
        });
        projectMessage.subscribe(CoolRequestIdeaTopic.REQUEST_SEND_END, (CoolRequestIdeaTopic.ObjectListener) content -> {
            String id = "";
            if (content instanceof String) id = content.toString();
            if (content instanceof Controller) id = ((Controller) content).getId();
            if (StringUtils.isEqualsIgnoreCase(getCurrentController().getId(), id)) {
                sendRequestButton.setLoadingStatus(false);
            }
        });
        //检测到有响应结果，则改变button状态
        projectMessage.subscribe(CoolRequestIdeaTopic.HTTP_RESPONSE,
                (CoolRequestIdeaTopic.HttpResponseEventListener) (requestId, invokeResponseModel) -> {
                    if (StringUtils.isEqualsIgnoreCase(getCurrentController().getId(), requestId)) {
                        sendRequestButton.setLoadingStatus(false);
                    }
                });

        //检测到环境发生改变，则重置环境
        projectMessage.subscribe(CoolRequestIdeaTopic.ENVIRONMENT_CHANGE, (CoolRequestIdeaTopic.BaseListener) () -> {
            if (controller != null) {
                runLoadControllerInfoOnMain(controller);
            }
        });
        //检测到controller选择则重置信息
        projectMessage.subscribe(CoolRequestIdeaTopic.COMPONENT_CHOOSE_EVENT, new CoolRequestIdeaTopic.ComponentChooseEventListener() {
            @Override
            public void onChooseEvent(Component component) {
                if (component instanceof Controller) {
                    runLoadControllerInfoOnMain(((Controller) component));
                }
            }

            @Override
            public void refreshEvent(Component component) {
                if (component == controller) {
                    runLoadControllerInfoOnMain(controller);
                }
            }
        });
        //检测到有Controller增加，则更新数据
        projectMessage.subscribe(CoolRequestIdeaTopic.ADD_SPRING_REQUEST_MAPPING_MODEL, (CoolRequestIdeaTopic.SpringRequestMappingModel) controllers -> {
            if (controller != null && !(controllers instanceof CustomController)) {
                for (Controller item : controllers) {
                    if (StringUtils.isEqualsIgnoreCase(item.getId(), controller.getId())) {
                        runLoadControllerInfoOnMain(item);
                        return;
                    }
                }
            }
            runLoadControllerInfoOnMain(null);
        });
        projectMessage.subscribe(CoolRequestIdeaTopic.COMPONENT_ADD, (CoolRequestIdeaTopic.ComponentAddEvent) (components, componentType) -> {
            if (controller == null) return;
            if (componentType == ComponentType.CONTROLLER) {
                for (Component component : components) {
                    if (component instanceof DynamicController && component.getId().equalsIgnoreCase(controller.getId())) {
                        controller = ((DynamicController) component);
                    }
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
        final JPanel httpParamInputPanel = new JPanel();
        httpParamInputPanel.setLayout(new BorderLayout(0, 0));

        modelSelectPanel.add(httpInvokeModelComboBox, BorderLayout.WEST);
        modelSelectPanel.add(requestMethodComboBox, BorderLayout.CENTER);
        requestUrlTextField.setColumns(45);
        requestUrlTextField.setText("");
        httpParamInputPanel.add(modelSelectPanel, BorderLayout.WEST);
        httpParamInputPanel.add(requestUrlTextField);
        httpParamInputPanel.add(sendRequestButton, BorderLayout.EAST);
        add(httpParamInputPanel, BorderLayout.NORTH);

        httpParamTab = new JBTabsImpl(project);

        //request header input page
        requestParamApply.add(requestHeaderPage);
        headTab = new TabInfo(requestHeaderPage);
        headTab.setText("Header");
        httpParamTab.addTab(headTab);

        //url param input page
        requestParamApply.add(urlParamPage);
        urlParamPageTabInfo = new TabInfo(urlParamPage);
        urlParamPageTabInfo.setText("Param");
        httpParamTab.addTab(urlParamPageTabInfo);

        requestParamApply.add(urlPathParamPage);
        urlPathParamPageTabInfo = new TabInfo(urlPathParamPage);
        urlPathParamPageTabInfo.setText("Path");
        httpParamTab.addTab(urlPathParamPageTabInfo);


        //request body input page
        requestBodyPage = new RequestBodyPage(project);
        requestParamApply.add(requestBodyPage);
        requestBodyTabInfo = new TabInfo(requestBodyPage);
        requestBodyTabInfo.setText("Body");
        httpParamTab.addTab(requestBodyTabInfo);

        //script input page
        scriptPage = new ScriptPage(project, this);
        scriptTabInfo = new TabInfo(scriptPage);
        scriptTabInfo.setText("Script");
        httpParamTab.addTab(scriptTabInfo);

        add(httpParamTab.getComponent(), BorderLayout.CENTER);

        reflexSettingUIPanel = new ReflexSettingUIPanel(project);
        reflexInvokePanelTabInfo = new TabInfo(reflexSettingUIPanel.getRoot());
        reflexInvokePanelTabInfo.setText("Invoke Setting");

        sendRequestButton.addActionListener(this);

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
        return "http://localhost:" + controller.getServerPort() + controller.getContextPath();
    }

    private void loadControllerInfo(Controller controller) {
        if (!(controller instanceof TemporaryController)) {
            clearAllRequestParam();  //可以被清除所有数据，并重新加载
        }
        this.controller = controller;
        if (this.controller == null) return;
        //从缓存中获取按钮的状态，true表示可用，需要重置状态，因为有请求可能还处于发送状态
        boolean enabledSendButton = mainBottomHTTPInvokeViewPanel.canEnabledSendButton(controller.getId());
        this.sendRequestButton.setLoadingStatus(!enabledSendButton);

        //临时请求不加载任任何信息
        if (controller instanceof TemporaryController) {
            return;
        }
        //从缓存中加载以前的设置
        RequestCache requestCache = ComponentCacheManager.getRequestParamCache(controller.getId());

        String url = fixFullUrl(controller, requestCache, getBaseUrl(controller));
        RequestEnvironment selectRequestEnvironment = project.getUserData(CoolRequestConfigConstant.RequestEnvironmentProvideKey).getSelectRequestEnvironment();

        if (!(controller instanceof CustomController) && !(selectRequestEnvironment instanceof EmptyEnvironment)) {
            url = StringUtils.joinUrlPath(selectRequestEnvironment.getHostAddress(), StringUtils.removeHostFromUrl(url));
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
     * 当某个API发起后，缓存数据，但是当下次主机发生改变后，重新回复到最新得主句
     */
    @NotNull
    private String fixFullUrl(Controller controller, RequestCache requestCache, String base) {
        if (controller instanceof CustomController) return controller.getUrl();
        String url = requestCache != null ? requestCache.getUrl() : StringUtils.joinUrlPath(base, controller.getUrl());
        //如果有缓存，但是开头不是当前的主机、端口、和上下文,但是要保存请求参数
        if (requestCache != null && !url.startsWith(base)) {
            String query = "";
            try {
                query = new URL(url).getQuery();
            } catch (MalformedURLException ignored) {
            }
            if (query == null) query = "";
            url = StringUtils.joinUrlPath(base, controller.getUrl());
            if (!StringUtils.hasText(query)) {
                url = url + "?" + query;
            }
        }
        return url;
    }

    private RequestCache createDefaultRequestCache(Controller controller) {
        SpringMvcRequestMapping mvcRequestMapping = new SpringMvcRequestMapping();
        HttpRequestInfo httpRequestInfo = mvcRequestMapping.getHttpRequestInfo(project, controller);
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
                .withHeaders(requestParamManager.getHttpHeader())
                .withUrlParams(requestParamManager.getUrlParam())
                .withRequestBodyType(requestParamManager.getRequestBodyType().getValue())
                .withFormDataInfos(requestParamManager.getFormData())
                .withUrlencodedBody(requestParamManager.getUrlencodedBody())
                .withRequestBody(requestParamManager.getRequestBody())
                .withUrlPathParams(requestParamManager.getPathParam())
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
    public void saveAsCustomController() {
        String url = getUrl();
        if (!UrlUtils.isURL(url)) {
            MessagesWrapperUtils.showErrorDialog(ResourceBundleUtils.getString("invalid.url"), ResourceBundleUtils.getString("tip"));
            return;
        }
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
            //保存缓存
            ComponentCacheManager.storageRequestCache(customController.getId(), createRequestCache());
            CacheStorageService cacheStorageService = ApplicationManager.getApplication().getService(CacheStorageService.class);
            MainBottomHTTPResponseView mainBottomHTTPResponseView = ProviderManager.getProvider(ViewRegister.class, project)
                    .getView(MainBottomHTTPResponseView.class);

            if (mainBottomHTTPResponseView.getInvokeResponseModel() != null) {
                cacheStorageService.storageResponseCache(customController.getId(), mainBottomHTTPResponseView.getInvokeResponseModel());
            }
            //刷新自定义目录
            ApplicationManager.getApplication().getMessageBus().syncPublisher(CoolRequestIdeaTopic.REFRESH_CUSTOM_FOLDER).event();
            //触发controller选择事件，将临时api转化为Custom API
            project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.COMPONENT_CHOOSE_EVENT).onChooseEvent(customController);
        }
    }

    public <T extends Controller> T buildAsCustomController(Class<T> targetController) {
        T result = null;
        try {
            result = targetController.getConstructor().newInstance();
            result.setContextPath("");
            result.setHttpMethod(getHttpMethod().toString());
            result.setMethodName("");
            result.setOwnerPsiMethod(new ArrayList<>());
            result.setParamClassList(new ArrayList<>());
            result.setSimpleClassName("");
            result.setUrl(getUrl());
            result.setId(UUID.randomUUID().toString());
        } catch (Exception ignored) {
        }
        return result;
    }

    @Override
    public List<KeyValue> getPathParam() {
        return urlPathParamPage.getTableMap();
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
        for (KeyValue keyValue : getHttpHeader()) {
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
    public List<KeyValue> getUrlencodedBody() {
        return requestBodyPage.getUrlencodedBody();
    }

    @Override
    public List<KeyValue> getHttpHeader() {
        return requestHeaderPage.getTableMap();
    }

    @Override
    public List<KeyValue> getUrlParam() {
        return urlParamPage.getTableMap();
    }

    @Override
    public List<FormDataInfo> getFormData() {
        return requestBodyPage.getFormDataInfo();
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

}
