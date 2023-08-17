package com.hxl.plugin.springboot.invoke.view.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hxl.plugin.springboot.invoke.bean.BeanInvokeSetting;
import com.hxl.plugin.springboot.invoke.bean.ProjectRequestBean;
import com.hxl.plugin.springboot.invoke.bean.SpringMvcRequestMappingEndpoint;
import com.hxl.plugin.springboot.invoke.bean.SpringMvcRequestMappingEndpointPlus;
import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;
import com.hxl.plugin.springboot.invoke.invoke.RequestCache;
import com.hxl.plugin.springboot.invoke.listener.RequestSendEvent;
import com.hxl.plugin.springboot.invoke.net.HttpMethod;
import com.hxl.plugin.springboot.invoke.net.MapRequest;
import com.hxl.plugin.springboot.invoke.net.MediaTypes;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;
import com.hxl.plugin.springboot.invoke.utils.RequestParamCacheManager;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.view.MultilingualEditor;
import com.hxl.plugin.springboot.invoke.view.ReflexSettingUIPanel;
import com.hxl.plugin.springboot.invoke.view.page.*;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.impl.FileTypeRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import com.intellij.util.Consumer;
import com.intellij.util.ui.JBUI;
import okhttp3.Headers;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class HTTPRequestInfoPanel extends JPanel {
    public static final FileType DEFAULT_FILE_TYPE = MultilingualEditor.TEXT_FILE_TYPE;
    private static final String DEFAULT_REQUEST_HEADER = "{\n" +
            "    \"User-Agent\":\"SpringInvoke\"\n" +
            "}";
    private static final String IDENTITY_HEAD = "HEAD";
    private static final String IDENTITY_BODY = "BODY";
    private final Project project;

    private static final List<MapRequest> mapRequest =new ArrayList<>();
    private JComboBox<HttpMethod> requestMethodComboBox;
    private JTextField requestUrlTextField;
    private JButton sendRequestButton;
    private JBTabs httpParamTab;
    private MultilingualEditor requestHeaderEditor;
    private MultilingualEditor requestBodyEditor;
    private ComboBox<String> requestBodyFileTypeComboBox;
    private ComboBox<String> httpInvokeModelComboBox;
    private MultilingualEditor responseBodyEditor;
    private TabInfo reflexInvokePanelTabInfo;
    private SpringMvcRequestMappingEndpointPlus selectSpringMvcRequestMappingEndpoint;
    private ProjectRequestBean projectRequestBean;
    private ComboBox<FileType> responseBodyFileTypeComboBox;
    private TabInfo responseBodyTabInfo;
    private TabInfo responseHeaderTabInfo;

    private RequestSendEvent requestSendEvent;

    private String httpHeaderColName[] = {"Key", "Value", "操作"};
    private DefaultTableModel httpHeaderDefaultTableModel = new DefaultTableModel(null, httpHeaderColName);
    private JTable httpHeaderTable;
    public  void applyRequestInfo(ControllerInvoke.ControllerRequestData controllerRequestData){
        for (MapRequest request : mapRequest) {
            request.configRequest(controllerRequestData);
        }
        // TODO: 2023/8/17  
        
    }

    public HTTPRequestInfoPanel(Project project, RequestSendEvent requestSendEvent) {
        this.project = project;
        this.requestSendEvent = requestSendEvent;
        init();
        initEvent();
    }

    public String getRequestUrl() {
        return requestUrlTextField.getText();
    }

    public HttpMethod getHttpMethod() {
        return HttpMethod.parse(requestMethodComboBox.getSelectedItem());
    }

    public String getRequestBodyFileType() {
        return (String) requestBodyFileTypeComboBox.getSelectedItem();
    }

    public String getRequestBody() {
        return requestBodyEditor.getText();
    }

    public Map<String, Object> getRequestHeader() throws JsonProcessingException {
        if (getRequestHeaderAsString().length() == 0) return new HashMap<>();
        return ObjectMappingUtils.getInstance().readValue(getRequestHeaderAsString(), new TypeReference<>() {
        });
    }


    public MultilingualEditor getHttpResponseEditor() {
        return responseBodyEditor;
    }

    public String getRequestHeaderAsString() {
        return requestHeaderEditor.getText();
    }

    private void loadReflexInvokePanel(boolean show) {
        if (show && selectSpringMvcRequestMappingEndpoint != null) {
            ReflexSettingUIPanel reflexSettingUIPanel = (ReflexSettingUIPanel) reflexInvokePanelTabInfo.getComponent();
            reflexSettingUIPanel.setRequestMappingInvokeBean(selectSpringMvcRequestMappingEndpoint.getSpringMvcRequestMappingEndpoint());
            httpParamTab.addTab(reflexInvokePanelTabInfo);
            return;
        }
        httpParamTab.removeTab(reflexInvokePanelTabInfo);
    }

    private void initEvent() {
        // 发送请求按钮监听
        sendRequestButton.addActionListener(event -> requestSendEvent.sendRequest());
        requestBodyFileTypeComboBox.addItemListener(e -> {
            Object selectedObject = e.getItemSelectable().getSelectedObjects()[0];
            if (selectedObject instanceof String) {
                if (MediaTypes.APPLICATION_JSON.equalsIgnoreCase(selectedObject.toString())) {
                    requestBodyEditor.setFileType(MultilingualEditor.JSON_FILE_TYPE);
                }
            }
        });
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
            if (selectSpringMvcRequestMappingEndpoint != null)
                loadReflexInvokePanel(!"HTTP".equalsIgnoreCase(item.toString()));
        });
    }

//    private JComponent createRequestHeader() {
//        httpHeaderDefaultTableModel.addRow(new String[]{"","","Delete"});
//        httpHeaderTable = new JTable(httpHeaderDefaultTableModel) {
//            public boolean isCellEditable(int row, int column) {
//               return true;
//            }
//        };
//        Action delete = new AbstractAction()
//        {
//            public void actionPerformed(ActionEvent e)
//            {
//                JTable table = (JTable)e.getSource();
//                int modelRow = Integer.valueOf( e.getActionCommand() );
//                ((DefaultTableModel)table.getModel()).removeRow(modelRow);
//
//                if (table.getModel().getRowCount()==0) httpHeaderDefaultTableModel.addRow(new String[]{"", "", "Delete"});
//            }
//        };
//        httpHeaderDefaultTableModel.addTableModelListener(new TableModelListener() {
//            @Override
//            public void tableChanged(TableModelEvent e) {
//                if (e.getType()==TableModelEvent.UPDATE &&  e.getColumn()==0 && httpHeaderDefaultTableModel.getValueAt(httpHeaderDefaultTableModel.getRowCount()-1, 0).toString().length()!=0){
//                    String[] strings = {"", "", "Delete"};
//                    httpHeaderDefaultTableModel.addRow(strings);
//                }
//            }
//        });
//        ButtonColumn buttonColumn = new ButtonColumn(httpHeaderTable, delete, 2);
//        buttonColumn.setMnemonic(KeyEvent.VK_D);
//        httpHeaderTable.setSelectionBackground(Color.getColor("#00000000"));
//        httpHeaderTable.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
//        httpHeaderTable.setDefaultEditor(Object.class, new CustomTableCellEditor());
//        httpHeaderTable.setRowHeight(35);
////        httpHeaderTable.setSelectionBackground(Color.OPAQUE);
//        return new JScrollPane(new RequestHeaderPage());
//    }


    private void init() {
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

        RequestHeaderPage requestHeaderPage = new RequestHeaderPage();
        mapRequest.add(requestHeaderPage);   //映射请求头
        TabInfo headTab = new TabInfo(requestHeaderPage);
        headTab.setText("Header");
        httpParamTab.addTab(headTab);

        UrlParamPage urlParamPage1 = new UrlParamPage();  //映射url参数
        mapRequest.add(urlParamPage1);

        TabInfo urlParamPage = new TabInfo(urlParamPage1);
        urlParamPage.setText("Param");
        httpParamTab.addTab(urlParamPage);
//        JPanel requestBodyFileTypePanel = new JPanel(new BorderLayout());
//        requestBodyFileTypePanel.add(new JBLabel("Select Body Type"), BorderLayout.WEST);
//
//        requestBodyFileTypeComboBox.setFocusable(false);
//        requestBodyFileTypePanel.add(requestBodyFileTypeComboBox, BorderLayout.CENTER);
//        requestBodyFileTypePanel.setBorder(JBUI.Borders.emptyLeft(3));
//        add(requestBodyFileTypePanel, BorderLayout.SOUTH);
//
//
//        requestBodyEditor = new MultilingualEditor(project, DEFAULT_FILE_TYPE);
//        requestBodyEditor.setName(IDENTITY_BODY);
//
//        JPanel jPanel = new JPanel(new BorderLayout());
//        jPanel.add(requestBodyFileTypePanel, BorderLayout.SOUTH);
//        jPanel.add(requestBodyEditor, BorderLayout.CENTER);

        RequestBodyPage requestBodyPage = new RequestBodyPage(project);
        mapRequest.add(requestBodyPage);
        TabInfo requestBodyTabInfo = new TabInfo(requestBodyPage);
        requestBodyTabInfo.setText("Body");
        httpParamTab.addTab(requestBodyTabInfo);
        putClientProperty("nextFocus", requestBodyEditor);


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


    public void setSelectData(SpringMvcRequestMappingEndpointPlus springMvcRequestMappingEndpoint) {
        this.selectSpringMvcRequestMappingEndpoint = springMvcRequestMappingEndpoint;
        SpringMvcRequestMappingEndpoint endpoint = springMvcRequestMappingEndpoint.getSpringMvcRequestMappingEndpoint();
        String base = "http://localhost:" + springMvcRequestMappingEndpoint.getServerPort() + springMvcRequestMappingEndpoint.getContextPath();

        //从缓存中加载以前的设置
        RequestCache requestCache = RequestParamCacheManager.getCache(springMvcRequestMappingEndpoint.getSpringMvcRequestMappingEndpoint().getId());
        String url = requestCache != null ? requestCache.getUrl() : base + springMvcRequestMappingEndpoint.getSpringMvcRequestMappingEndpoint().getUrl();
        //如果有缓存，但是开头不是当前的主机、端口、和上下文,但是要保存请求参数
        if (requestCache != null && !url.startsWith(base)) {
            String query = "";
            try {
                query = new URL(url).getQuery();
            } catch (MalformedURLException ignored) {
            }
            if (query == null) query = "";
            url = base + springMvcRequestMappingEndpoint.getSpringMvcRequestMappingEndpoint().getUrl() + "?" + query;
        }

        requestUrlTextField.setText(url);
        httpInvokeModelComboBox.setSelectedIndex(requestCache != null ? requestCache.getInvokeModelIndex() : 0);
      //  requestBodyEditor.setText(requestCache != null ? requestCache.getRequestBody() : "");
//        try {
//            String value = requestCache != null ? ObjectMappingUtils.getInstance().writeValueAsString(requestCache.getRequestHeader()) : DEFAULT_REQUEST_HEADER;
//            requestHeaderEditor.setText(ObjectMappingUtils.format(value));
//        } catch (JsonProcessingException ignored) {
//        }
        requestMethodComboBox.setSelectedItem(HttpMethod.parse(endpoint.getHttpMethod().toUpperCase()));
        loadReflexInvokePanel(!"HTTP".equalsIgnoreCase(Objects.requireNonNull(httpInvokeModelComboBox.getSelectedItem()).toString()));
    }

    public int getInvokeModelIndex() {
        return httpInvokeModelComboBox.getSelectedIndex();
    }

    public BeanInvokeSetting getBeanInvokeSetting() {
        return ((ReflexSettingUIPanel) reflexInvokePanelTabInfo.getComponent()).getBeanInvokeSetting();
    }

    public MultilingualEditor getHttpHeaderEditor() {
        return ((MultilingualEditor) responseHeaderTabInfo.getComponent());
    }
}
