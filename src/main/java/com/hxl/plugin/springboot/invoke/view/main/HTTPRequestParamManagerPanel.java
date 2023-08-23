package com.hxl.plugin.springboot.invoke.view.main;

import com.hxl.plugin.springboot.invoke.bean.BeanInvokeSetting;
import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;
import com.hxl.plugin.springboot.invoke.invoke.RequestCache;
import com.hxl.plugin.springboot.invoke.listener.RequestSendEvent;
import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.model.SpringMvcRequestMappingInvokeBean;
import com.hxl.plugin.springboot.invoke.net.*;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;
import com.hxl.plugin.springboot.invoke.utils.PsiUtils;
import com.hxl.plugin.springboot.invoke.utils.RequestParamCacheManager;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.utils.param.*;
import com.hxl.plugin.springboot.invoke.view.IRequestParamManager;
import com.hxl.plugin.springboot.invoke.view.MultilingualEditor;
import com.hxl.plugin.springboot.invoke.view.ReflexSettingUIPanel;
import com.hxl.plugin.springboot.invoke.view.page.*;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.impl.FileTypeRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class HTTPRequestParamManagerPanel extends JPanel implements IRequestParamManager {
    public static final FileType DEFAULT_FILE_TYPE = MultilingualEditor.TEXT_FILE_TYPE;
    private static final String IDENTITY_HEAD = "HEAD";
    private static final String IDENTITY_BODY = "BODY";
    private final Project project;
    private static final List<MapRequest> mapRequest = new ArrayList<>();
    private static final List<RequestParamSpeculate> requestParamSpeculates =new ArrayList<>();
    private JComboBox<HttpMethod> requestMethodComboBox;
    private RequestHeaderPage requestHeaderPage;
    private JTextField requestUrlTextField;
    private JButton sendRequestButton;
    private JBTabs httpParamTab;
    private UrlParamPage urlParamPage;
    private RequestBodyPage requestBodyPage;
    private ComboBox<String> requestBodyFileTypeComboBox;
    private ComboBox<String> httpInvokeModelComboBox;
    private MultilingualEditor responseBodyEditor;
    private TabInfo reflexInvokePanelTabInfo;
    private RequestMappingModel requestMappingModel;
    private ComboBox<FileType> responseBodyFileTypeComboBox;
    private final RequestSendEvent requestSendEvent;

    public void applyRequestParams(ControllerInvoke.ControllerRequestData controllerRequestData) {
        for (MapRequest request : mapRequest) {
            request.configRequest(controllerRequestData);
        }
        controllerRequestData.setHeader("content-type", controllerRequestData.getContentType().toLowerCase(Locale.ROOT));
    }

    public HTTPRequestParamManagerPanel(Project project, RequestSendEvent requestSendEvent) {
        this.project = project;
        this.requestSendEvent = requestSendEvent;
        init();
        initEvent();
    }

    public HttpMethod getHttpMethod() {
        return HttpMethod.parse(requestMethodComboBox.getSelectedItem());
    }

    public String getRequestBody() {
        return requestBodyPage.getTextRequestBody();
    }

    public MultilingualEditor getHttpResponseEditor() {
        return responseBodyEditor;
    }

    private void loadReflexInvokePanel(boolean show) {
        if (show && requestMappingModel != null) {
            ReflexSettingUIPanel reflexSettingUIPanel = (ReflexSettingUIPanel) reflexInvokePanelTabInfo.getComponent();
            reflexSettingUIPanel.setRequestMappingInvokeBean(requestMappingModel.getController());
            httpParamTab.addTab(reflexInvokePanelTabInfo);
            return;
        }
        httpParamTab.removeTab(reflexInvokePanelTabInfo);
    }

    private void initEvent() {
        // 发送请求按钮监听
        sendRequestButton.addActionListener(event -> requestSendEvent.sendRequest());
        responseBodyFileTypeComboBox.setRenderer(new FileTypeRenderer());
        responseBodyFileTypeComboBox.addItemListener(e -> {
            Object selectedObject = e.getItemSelectable().getSelectedObjects()[0];
            if (selectedObject instanceof FileType) {
                FileType fileType = (FileType) selectedObject;
                responseBodyEditor.setFileType(fileType);
                if (MultilingualEditor.JSON_FILE_TYPE.equals(fileType)) {
                    responseBodyEditor.setText(ObjectMappingUtils.format(responseBodyEditor.getText()));
                }
            }
        });
        httpInvokeModelComboBox.addItemListener(e -> {
            Object item = e.getItem();
            if (requestMappingModel != null)
                loadReflexInvokePanel(!"HTTP".equalsIgnoreCase(item.toString()));
        });
    }

    private void init() {
        requestParamSpeculates.add(new UrlParamSpeculate());
        requestParamSpeculates.add(new HeaderParamSpeculate());
        requestParamSpeculates.add(new JsonBodyParamSpeculate());
        requestParamSpeculates.add(new MultipartFileParamSpeculate());

        setLayout(new BorderLayout(0, 0));
        //http参数面板
        final JPanel httpParamInputPanel = new JPanel();
        httpParamInputPanel.setLayout(new BorderLayout(0, 0));
        requestUrlTextField = new JBTextField();
        sendRequestButton = new JButton("send");
        requestBodyFileTypeComboBox = createRequestTypeComboBox();
        responseBodyFileTypeComboBox = createTextTypeComboBox();
        //httpInvokeModel和requestMethod容器
        JPanel modelSelectPanel = new JPanel(new BorderLayout());
        requestMethodComboBox = new ComboBox<>(HttpMethod.getValues());
        httpInvokeModelComboBox = new ComboBox<>(new String[]{"http", ResourceBundleUtils.getString("object.reflex")});

        modelSelectPanel.add(httpInvokeModelComboBox, BorderLayout.WEST);
        modelSelectPanel.add(requestMethodComboBox, BorderLayout.CENTER);
        requestUrlTextField.setColumns(45);

        httpParamInputPanel.add(modelSelectPanel, BorderLayout.WEST);
        httpParamInputPanel.add(requestUrlTextField);
        httpParamInputPanel.add(sendRequestButton, BorderLayout.EAST);
        add(httpParamInputPanel, BorderLayout.NORTH);


        //请求头
        httpParamTab = new JBTabsImpl(project);

        requestHeaderPage = new RequestHeaderPage();
        mapRequest.add(requestHeaderPage);   //映射请求头
        TabInfo headTab = new TabInfo(requestHeaderPage);
        headTab.setText("Header");
        httpParamTab.addTab(headTab);

        urlParamPage = new UrlParamPage();  //映射url参数
        mapRequest.add(urlParamPage);

        TabInfo urlParamPageTabInfo = new TabInfo(urlParamPage);
        urlParamPageTabInfo.setText("Param");
        httpParamTab.addTab(urlParamPageTabInfo);

        requestBodyPage = new RequestBodyPage(project);
        mapRequest.add(requestBodyPage);
        TabInfo requestBodyTabInfo = new TabInfo(requestBodyPage);
        requestBodyTabInfo.setText("Body");
        httpParamTab.addTab(requestBodyTabInfo);
        responseBodyEditor = new MultilingualEditor(project);
        JPanel responseBodyFileTypePanel = new JPanel(new BorderLayout());
        responseBodyFileTypePanel.add(new JBLabel("Select Body Type"), BorderLayout.WEST);

        responseBodyFileTypeComboBox.setFocusable(false);
        responseBodyFileTypePanel.add(responseBodyFileTypeComboBox, BorderLayout.CENTER);
        responseBodyFileTypePanel.setBorder(JBUI.Borders.emptyLeft(3));
        JPanel responsePanel = new JPanel(new BorderLayout());
        responsePanel.add(responseBodyFileTypePanel, BorderLayout.SOUTH);
        responseBodyEditor.setComponentPopupMenu(new JPopupMenu());
        responsePanel.add(responseBodyEditor, BorderLayout.CENTER);

//        responseBodyTabInfo = new TabInfo(responsePanel);
//        responseBodyTabInfo.setText("Response");
//        httpParamTab.addTab(responseBodyTabInfo);
        add(httpParamTab.getComponent(), BorderLayout.CENTER);

//        responseHeaderTabInfo = new TabInfo(new MultilingualEditor(project, MultilingualEditor.TEXT_FILE_TYPE));
//        responseHeaderTabInfo.setText("Response Header");
//        httpParamTab.addTab(responseHeaderTabInfo);
        reflexInvokePanelTabInfo = new TabInfo(new ReflexSettingUIPanel());
        reflexInvokePanelTabInfo.setText("Invoke Setting");
    }

    private ComboBox<String> createRequestTypeComboBox() {
        return new ComboBox<>(new String[]{MediaTypes.APPLICATION_JSON, MediaTypes.APPLICATION_FORM, MediaTypes.TEXT});
    }

    private ComboBox<FileType> createTextTypeComboBox() {
        ComboBox<FileType> fileTypeComboBox = new ComboBox<>(new FileType[]{
                MultilingualEditor.TEXT_FILE_TYPE,
                MultilingualEditor.JSON_FILE_TYPE,
                MultilingualEditor.HTML_FILE_TYPE,
                MultilingualEditor.XML_FILE_TYPE
        });
        fileTypeComboBox.setRenderer(new FileTypeRenderer());
        return fileTypeComboBox;
    }

    private void clearRequestParam() {
        requestBodyPage.setJsonBodyText("");
        requestBodyPage.setXmlBodyText("");
        requestBodyPage.setRawBodyText("");

    }

    public IRequestParamManager getRequestParamManager() {
        return this;
    }

    public void setSelectData(RequestMappingModel requestMappingModel) {
        this.requestMappingModel = requestMappingModel;
        SpringMvcRequestMappingInvokeBean invokeBean = requestMappingModel.getController();
        String base = "http://localhost:" + requestMappingModel.getServerPort() + requestMappingModel.getContextPath();
        clearRequestParam();
        //从缓存中加载以前的设置
        RequestCache requestCache = RequestParamCacheManager.getCache(requestMappingModel.getController().getId());
        String url = requestCache != null ? requestCache.getUrl() : base + requestMappingModel.getController().getUrl();
        //如果有缓存，但是开头不是当前的主机、端口、和上下文,但是要保存请求参数
        if (requestCache != null && !url.startsWith(base)) {
            String query = "";
            try {
                query = new URL(url).getQuery();
            } catch (MalformedURLException ignored) {
            }
            if (query == null) query = "";
            url = base + requestMappingModel.getController().getUrl() + "?" + query;
        }

        requestUrlTextField.setText(url);
        if (requestCache == null) requestCache = createDefaultRequestCache(requestMappingModel);

        getRequestParamManager().setInvokeHttpMethod(requestCache.getInvokeModelIndex());//调用方式
        getRequestParamManager().setHttpMethod(HttpMethod.parse(invokeBean.getHttpMethod().toUpperCase()));//http接口
        getRequestParamManager().setHttpHeader(requestCache.getHeaders());
        getRequestParamManager().setUrlParam(requestCache.getUrlParams());
        getRequestParamManager().setRequestBodyType(requestCache.getRequestBodyType());
        getRequestParamManager().setUrlencodedBody(requestCache.getUrlencodedBody());
        getRequestParamManager().setFormData(requestCache.getFormDataInfos());
        getRequestParamManager().setRequestBody(requestCache.getRequestBodyType(), requestCache.getRequestBody());
        //是否显示反射设置面板
        Object selectedItem = httpInvokeModelComboBox.getSelectedItem();
        loadReflexInvokePanel(!"HTTP".equalsIgnoreCase(selectedItem == null ? "" : selectedItem.toString()));
    }

    /**
     * 推断
     * @return
     */
    private RequestCache createDefaultRequestCache(RequestMappingModel requestMappingModel) {
        PsiClass psiClass = PsiUtils.findClassByName(project, requestMappingModel.getController().getSimpleClassName());
        if (psiClass !=null){
            PsiMethod methodInClass = PsiUtils.findMethodInClass(psiClass, requestMappingModel.getController().getMethodName());
            RequestCache.RequestCacheBuilder requestCacheBuilder = RequestCache.RequestCacheBuilder.aRequestCache()
                    .withInvokeModelIndex(1);
            for (RequestParamSpeculate requestParamSpeculate : requestParamSpeculates) {
                requestParamSpeculate.set(methodInClass,requestCacheBuilder);
            }
            return requestCacheBuilder.build();
        }
        return RequestCache.RequestCacheBuilder.aRequestCache()
                .withInvokeModelIndex(1)
                .withHeaders(new ArrayList<>())
                .withUrlParams(new ArrayList<>())
                .withRequestBodyType("application/json")
                .withFormDataInfos(new ArrayList<>())
                .withRequestBodyType("json")
                .build();
    }

    public int getInvokeModelIndex() {
        return httpInvokeModelComboBox.getSelectedIndex();
    }

    public BeanInvokeSetting getBeanInvokeSetting() {
        return ((ReflexSettingUIPanel) reflexInvokePanelTabInfo.getComponent()).getBeanInvokeSetting();
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
        requestMethodComboBox.setSelectedItem(method);
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
    public void setFormData(List<FormDataInfo> value) {
        requestBodyPage.setFormData(Optional.ofNullable(value).orElse(new ArrayList<>()));
    }

    @Override
    public void setUrlencodedBody(List<KeyValue> value) {
        requestBodyPage.setUrlencodedBodyTableData(Optional.ofNullable(value).orElse(new ArrayList<>()));
    }

    @Override
    public void setRequestBody(String type, String body) {
        if (type == null || body == null) return;
        if (type.contains("json"))  {
            requestBodyPage.setJsonBodyText(ObjectMappingUtils.format(body));
            return;
        }
        if (type.contains("xml"))  {
            requestBodyPage.setXmlBodyText(body);
            return;
        }
        requestBodyPage.setRawBodyText(body);
    }

    @Override
    public String getRequestBodyType() {
        return requestBodyPage.getSelectedRequestBodyType();
    }

    @Override
    public void setRequestBodyType(String type) {
        requestBodyPage.setRequestBodyType(type);
    }
}
