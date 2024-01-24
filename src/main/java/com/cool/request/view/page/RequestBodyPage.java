package com.cool.request.view.page;

import com.cool.request.net.FormDataInfo;
import com.cool.request.net.KeyValue;
import com.cool.request.net.MediaTypes;
import com.cool.request.net.RequestParamApply;
import com.cool.request.net.request.HttpRequestParamUtils;
import com.cool.request.net.request.StandardHttpRequestParam;
import com.cool.request.springmvc.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.Consumer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestBodyPage extends JPanel implements RequestParamApply {
    private final Map<String, ContentTypeConvert> httpParamRequestBodyConvert = new HashMap<>();
    private final List<String> sortParam = new ArrayList<>();

    private final Project project;
    private final Map<String, JRadioButton> radioButtons = new HashMap<>();
    private JSONRequestBodyPage jsonRequestBodyPage;
    private XmlParamRequestBodyPage xmlParamRequestBodyPage;
    private RawParamRequestBodyPage rawParamRequestBodyPage;
    private FormDataRequestBodyPage formDataRequestBodyPage;
    private JPanel nonePanel = new JPanel();
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

    public RequestBodyPage(Project project) {
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
        if (contentTypeConvert instanceof NoneDataContentTypeConvert) return;
        standardHttpRequestParam.setBody(contentTypeConvert.getBody(standardHttpRequestParam));

//        //防止空form-data
        if (contentTypeConvert instanceof FormDataContentTypeConvert) {
            List<FormDataInfo> infoList = ((FormBody) standardHttpRequestParam.getBody()).getData();
            if (infoList.isEmpty()) {
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
        addNewHttpRequestParamPage("None", new NoneDataContentTypeConvert(), nonePanel);
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
            Body originBody = standardHttpRequestParam.getBody();
            if (!(originBody instanceof FormUrlBody)) return new FormUrlBody(urlencodedRequestBodyPage.getTableMap());
            ((FormUrlBody) originBody).getData().addAll(urlencodedRequestBodyPage.getTableMap());
            return ((FormUrlBody) originBody);

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
            Body body = standardHttpRequestParam.getBody();
            if (!(body instanceof FormBody)) return new FormBody(formDataRequestBodyPage.getFormData());
            ((FormBody) body).getData().addAll(formDataRequestBodyPage.getFormData());
            return ((FormBody) body);
        }
    }

    class NoneDataContentTypeConvert implements ContentTypeConvert {
        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public Body getBody(StandardHttpRequestParam standardHttpRequestParam) {
            return new EmptyBody();
        }
    }
}
