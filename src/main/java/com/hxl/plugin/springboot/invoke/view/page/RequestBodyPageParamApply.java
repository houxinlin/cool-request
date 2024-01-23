package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.net.FormDataInfo;
import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.hxl.plugin.springboot.invoke.net.RequestParamApply;
import com.hxl.plugin.springboot.invoke.net.MediaTypes;
import com.hxl.plugin.springboot.invoke.net.request.HttpRequestParamUtils;
import com.hxl.plugin.springboot.invoke.net.request.StandardHttpRequestParam;
import com.hxl.plugin.springboot.invoke.springmvc.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.Consumer;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class RequestBodyPageParamApply extends JPanel implements RequestParamApply {
    private final Map<String, ContentTypeConvert> httpParamRequestBodyConvert = new HashMap<>();
    private final List<String> sortParam = new ArrayList<>();

    private final Project project;
    private final Map<String, JRadioButton> radioButtons = new HashMap<>();
    private JSONRequestBodyPage jsonRequestBodyPage;
    private XmlParamRequestBodyPage xmlParamRequestBodyPage;
    private RawParamRequestBodyPage rawParamRequestBodyPage;
    private FormDataRequestBodyPage formDataRequestBodyPage;
    private BinaryRequestBodyPage binaryRequestBodyPage;
    private FormUrlencodedRequestBodyPage urlencodedRequestBodyPage;
    private final JPanel topHttpParamTypeContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPageJPanel = new JPanel(cardLayout);
    private final ContentTypeConvert EMPTY_CONTENT_TYPE_CONVERT = new ContentTypeConvert() {
    };
    private final ButtonGroup buttonGroup = new ButtonGroup();

    private final Consumer<String> radioButtonConsumer = s -> {
        cardLayout.show(contentPageJPanel, s);
    };

    public RequestBodyPageParamApply(Project project) {
        this.project = project;
        init();
    }

    private void addNewHttpRequestParamPage(String name,
                                            ContentTypeConvert contentTypeConvert, JPanel panel) {
        contentPageJPanel.add(name, panel);
        sortParam.add(name);

        JRadioButton jRadioButton = createJRadioButton(name, radioButtonConsumer);
        radioButtons.put(name, jRadioButton);
        buttonGroup.add(jRadioButton);
        topHttpParamTypeContainer.add(jRadioButton);
        httpParamRequestBodyConvert.put(name, contentTypeConvert);
    }

    private JRadioButton createJRadioButton(String name, Consumer<String> callable) {
        JRadioButton jRadioButton = new JRadioButton(name);
        jRadioButton.addActionListener(e -> callable.consume(name));
        return jRadioButton;
    }

    private String getChooseRequestBodyType() {
        for (String key : radioButtons.keySet()) {
            if (radioButtons.get(key).isSelected()) {
                return key;
            }
        }
        return null;
    }

    @Override
    public void configRequest(StandardHttpRequestParam standardHttpRequestParam) {
        //设置content-type
        String chooseRequestBodyType = getChooseRequestBodyType();

        ContentTypeConvert contentTypeConvert = httpParamRequestBodyConvert.getOrDefault(chooseRequestBodyType, EMPTY_CONTENT_TYPE_CONVERT);
        standardHttpRequestParam.setBody(contentTypeConvert.getBody(standardHttpRequestParam));

        //防止空form-data
        if (contentTypeConvert instanceof FormDataContentTypeConvert) {
            if (formDataRequestBodyPage.getFormData().isEmpty()) {
                HttpRequestParamUtils.setContentType(standardHttpRequestParam, MediaTypes.APPLICATION_WWW_FORM);
                return;
            }
        }
        HttpRequestParamUtils.setContentType(standardHttpRequestParam, contentTypeConvert.getContentType());
    }

    public void init() {
        setLayout(new BorderLayout());

        jsonRequestBodyPage = new JSONRequestBodyPage(this.project);
        xmlParamRequestBodyPage = new XmlParamRequestBodyPage(this.project);
        rawParamRequestBodyPage = new RawParamRequestBodyPage(this.project);
        urlencodedRequestBodyPage = new FormUrlencodedRequestBodyPage(this.project);
        formDataRequestBodyPage = new FormDataRequestBodyPage(this.project);
        binaryRequestBodyPage = new BinaryRequestBodyPage(this.project);
        addNewHttpRequestParamPage("form-data", new FormDataContentTypeConvert(), formDataRequestBodyPage);
        addNewHttpRequestParamPage("x-www-form-urlencoded",
                new FormUrlEncodedContentTypeConvert(), urlencodedRequestBodyPage);
        addNewHttpRequestParamPage("json", new ApplicationJSONContentTypeConvert(), jsonRequestBodyPage);
        addNewHttpRequestParamPage("xml", new XmlContentTypeConvert(), xmlParamRequestBodyPage);
        addNewHttpRequestParamPage("raw", new RawContentTypeConvert(), rawParamRequestBodyPage);
        addNewHttpRequestParamPage("binary", new BinaryContentTypeConvert(), binaryRequestBodyPage);

        setRequestBodyType("json");
        buttonGroup.setSelected(radioButtons.get("json").getModel(), true);

        add(topHttpParamTypeContainer, BorderLayout.NORTH);
        add(contentPageJPanel, BorderLayout.CENTER);
    }

    public String getSelectedRequestBodyType() {
        for (String name : radioButtons.keySet()) {
            if (radioButtons.get(name).isSelected()) return name;
        }
        return "";
    }

    public List<FormDataInfo> getFormDataInfo() {
        return formDataRequestBodyPage.getFormData();
    }

    public List<KeyValue> getUrlencodedBody() {
        return urlencodedRequestBodyPage.getTableMap();
    }

    public String getTextRequestBody() {
        if (radioButtons.get("json").isSelected()) return jsonRequestBodyPage.getText();
        if (radioButtons.get("xml").isSelected()) return xmlParamRequestBodyPage.getText();
        if (radioButtons.get("raw").isSelected()) return rawParamRequestBodyPage.getText();
        if (radioButtons.get("binary").isSelected()) return binaryRequestBodyPage.getSelectFile();
        return "";
    }

    private void showBodyPageAdapter(String type) {
        if (MediaTypes.APPLICATION_JSON.equalsIgnoreCase(type)) type = "json";
        if (MediaTypes.APPLICATION_WWW_FORM.equalsIgnoreCase(type)) type = "x-www-form-urlencoded";
        if (MediaTypes.TEXT.equalsIgnoreCase(type)) type = "raw";
        if (MediaTypes.APPLICATION_XML.equalsIgnoreCase(type)) type = "xml";
        if (MediaTypes.MULTIPART_FORM_DATA.equalsIgnoreCase(type)) type = "form-data";
        if (MediaTypes.APPLICATION_STREAM.equalsIgnoreCase(type)) type = "binary";
        showBodyPage(type);
    }

    public FormDataRequestBodyPage getFormDataRequestBodyPage() {
        return formDataRequestBodyPage;
    }

    public FormUrlencodedRequestBodyPage getUrlencodedRequestBodyPage() {
        return urlencodedRequestBodyPage;
    }

    private void showBodyPage(String type) {
        if (!radioButtons.containsKey(type)) {
            type = "json";
        }
        radioButtons.get(type).setSelected(true);
        cardLayout.show(contentPageJPanel, type);
    }

    public void setRequestBodyType(String requestBodyType) {
        if (requestBodyType == null) return;
        showBodyPageAdapter(requestBodyType);

    }

    public void setJsonBodyText(String textBody) {
        jsonRequestBodyPage.setText(textBody);
    }

    public void setXmlBodyText(String textBody) {
        xmlParamRequestBodyPage.setText(textBody);
    }

    public void setRawBodyText(String textBody) {
        rawParamRequestBodyPage.setText(textBody);
    }

    public void setBinaryRequestBodyFile(String file) {
        binaryRequestBodyPage.setSelectFile(file);
    }

    public void setUrlencodedBodyTableData(List<KeyValue> urlencodedBody) {
        urlencodedRequestBodyPage.setTableData(urlencodedBody);
    }

    public void setFormData(List<FormDataInfo> formDataInfos) {
        formDataRequestBodyPage.removeAllRow();
        formDataRequestBodyPage.setFormData(formDataInfos);
    }

    interface ContentTypeConvert {
        default public String getContentType() {
            return "text/paint";
        }

        default public Body getBody(StandardHttpRequestParam standardHttpRequestParam) {
            return new EmptyBody();
        }
    }

    class ApplicationJSONContentTypeConvert implements ContentTypeConvert {
        @Override
        public String getContentType() {
            return "application/json";
        }

        @Override
        public StringBody getBody(StandardHttpRequestParam standardHttpRequestParam) {
            return new StringBody(jsonRequestBodyPage.getText());
        }
    }

    class FormUrlEncodedContentTypeConvert implements ContentTypeConvert {
        @Override
        public String getContentType() {
            return "application/x-www-form-urlencoded";
        }

        @Override
        public FormUrlBody getBody(StandardHttpRequestParam standardHttpRequestParam) {
            return new FormUrlBody(urlencodedRequestBodyPage.getTableMap());
        }
    }

    class XmlContentTypeConvert implements ContentTypeConvert {
        @Override
        public String getContentType() {
            return "application/xml";
        }

        @Override
        public XMLBody getBody(StandardHttpRequestParam standardHttpRequestParam) {
            return new XMLBody(xmlParamRequestBodyPage.getText());
        }
    }

    class BinaryContentTypeConvert implements ContentTypeConvert {
        @Override
        public String getContentType() {
            return "application/octet-stream";
        }

        @Override
        public BinaryBody getBody(StandardHttpRequestParam standardHttpRequestParam) {
            return new BinaryBody(binaryRequestBodyPage.getSelectFile());
        }
    }

    class RawContentTypeConvert implements ContentTypeConvert {

        @Override
        public StringBody getBody(StandardHttpRequestParam standardHttpRequestParam) {
            return new StringBody(rawParamRequestBodyPage.getText());
        }
    }

    class FormDataContentTypeConvert implements ContentTypeConvert {
        @Override
        public String getContentType() {
            return "multipart/form-data";
        }

        @Override
        public FormBody getBody(StandardHttpRequestParam standardHttpRequestParam) {
            return new FormBody(formDataRequestBodyPage.getFormData());
        }
    }
}
