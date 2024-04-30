/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * RequestBodyPage.java is part of Cool Request
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

package com.cool.request.view.page;

import com.cool.request.components.http.FormDataInfo;
import com.cool.request.components.http.KeyValue;
import com.cool.request.components.http.net.*;
import com.cool.request.components.http.net.request.HttpRequestParamUtils;
import com.cool.request.components.http.net.request.StandardHttpRequestParam;
import com.cool.request.lib.springmvc.*;
import com.cool.request.view.main.IRequestParamManager;
import com.cool.request.view.table.RowDataState;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.util.Consumer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestBodyPage extends JPanel implements RequestParamApply, Disposable {
    private final Map<String, ContentTypeConvert> httpParamRequestBodyConvert = new HashMap<>();
    private final List<String> sortParam = new ArrayList<>();

    private final Project project;
    private final Map<String, JRadioButton> radioButtons = new HashMap<>();
    private JSONRequestBodyPage jsonRequestBodyPage;
    private XmlParamRequestBodyPage xmlParamRequestBodyPage;
    private RawParamRequestBodyPage rawParamRequestBodyPage;
    private FormDataRequestBodyPage formDataRequestBodyPage;
    private final JPanel nonePanel = new JPanel();
    private BinaryRequestBodyPage binaryRequestBodyPage;
    private FormUrlencodedRequestBodyPage urlencodedRequestBodyPage;
    private final JPanel topHttpParamTypeContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPageJPanel = new JPanel(cardLayout);
    private final ContentTypeConvert EMPTY_CONTENT_TYPE_CONVERT = new ContentTypeConvert() {
    };
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private IRequestParamManager iRequestParamManager;

    private final Consumer<String> radioButtonConsumer = s -> {
        cardLayout.show(contentPageJPanel, s);
    };

    @Override
    public void dispose() {
        xmlParamRequestBodyPage.dispose();
        jsonRequestBodyPage.dispose();
        rawParamRequestBodyPage.dispose();
    }

    public RequestBodyPage(Project project, IRequestParamManager iRequestParamManager) {
        this.project = project;
        this.iRequestParamManager = iRequestParamManager;
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

    private String getChooseRequestBodyTypeOfShort() {
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
        String chooseRequestBodyType = getChooseRequestBodyTypeOfShort();

        ContentTypeConvert contentTypeConvert = httpParamRequestBodyConvert.getOrDefault(chooseRequestBodyType, EMPTY_CONTENT_TYPE_CONVERT);
        Body body = contentTypeConvert.getBody(standardHttpRequestParam);
        //  防止空form-data
        if (body instanceof FormBody) {
            List<FormDataInfo> data = ((FormBody) body).getData();
            if (data.isEmpty()) {
                return;
            }
        }
        standardHttpRequestParam.setBody(body);
        if (!(body instanceof EmptyBody)) {
            HttpRequestParamUtils.setContentType(standardHttpRequestParam, contentTypeConvert.getContentType());
        }
    }

    public void init() {
        setLayout(new BorderLayout());

        jsonRequestBodyPage = new JSONRequestBodyPage(this.project);
        xmlParamRequestBodyPage = new XmlParamRequestBodyPage(this.project);
        rawParamRequestBodyPage = new RawParamRequestBodyPage(this.project);
        urlencodedRequestBodyPage = new FormUrlencodedRequestBodyPage(iRequestParamManager);
        formDataRequestBodyPage = new FormDataRequestBodyPage(this.project,iRequestParamManager);
        binaryRequestBodyPage = new BinaryRequestBodyPage(this.project);
        addNewHttpRequestParamPage("None", new NoneDataContentTypeConvert(), nonePanel);
        addNewHttpRequestParamPage("form-data", new FormDataContentTypeConvert(), formDataRequestBodyPage);
        addNewHttpRequestParamPage("x-www-form-urlencoded",
                new FormUrlEncodedContentTypeConvert(), urlencodedRequestBodyPage);
        addNewHttpRequestParamPage("json", new ApplicationJSONContentTypeConvert(), jsonRequestBodyPage);
        addNewHttpRequestParamPage("xml", new XmlContentTypeConvert(), xmlParamRequestBodyPage);
        addNewHttpRequestParamPage("raw", new RawContentTypeConvert(), rawParamRequestBodyPage);
        addNewHttpRequestParamPage("binary", new BinaryContentTypeConvert(), binaryRequestBodyPage);

        buttonGroup.setSelected(radioButtons.get("None").getModel(), true);

        add(topHttpParamTypeContainer, BorderLayout.NORTH);
        add(contentPageJPanel, BorderLayout.CENTER);
    }

    public MediaType getSelectedRequestBodyMediaType() {
        for (String name : radioButtons.keySet()) {
            if (radioButtons.get(name).isSelected()) {
                return new MediaType(httpParamRequestBodyConvert.get(name).getContentType());
            }
        }
        return null;
    }

    public List<FormDataInfo> getFormDataInfo(RowDataState rowDataState) {
        return formDataRequestBodyPage.getFormData(rowDataState);
    }

    public List<KeyValue> getUrlencodedBody() {
        return urlencodedRequestBodyPage.getTableMap(RowDataState.available);
    }

    public String getTextRequestBody() {
        if (radioButtons.get("json").isSelected()) return jsonRequestBodyPage.getText();
        if (radioButtons.get("xml").isSelected()) return xmlParamRequestBodyPage.getText();
        if (radioButtons.get("raw").isSelected()) return rawParamRequestBodyPage.getText();
        if (radioButtons.get("binary").isSelected()) return binaryRequestBodyPage.getSelectFile();
        return "";
    }

    public void switchRequestBodyType(MediaType mediaType) {
        if (mediaType == null || "none".equalsIgnoreCase(mediaType.getValue())) {
            showBodyPage("None");
            return;
        }
        showBodyPageAdapter(mediaType.getValue());
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
        if (!radioButtons.containsKey(type)) type = "None";
        radioButtons.get(type).setSelected(true);
        cardLayout.show(contentPageJPanel, type);
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
        formDataRequestBodyPage.setFormData(formDataInfos);
    }

    interface ContentTypeConvert {
        default public String getContentType() {
            return MediaTypes.TEXT;
        }

        default public Body getBody(StandardHttpRequestParam standardHttpRequestParam) {
            return new EmptyBody();
        }
    }

    class ApplicationJSONContentTypeConvert implements ContentTypeConvert {
        @Override
        public String getContentType() {
            return MediaTypes.APPLICATION_JSON;
        }

        @Override
        public JSONBody getBody(StandardHttpRequestParam standardHttpRequestParam) {
            return new JSONBody(jsonRequestBodyPage.getText());
        }
    }

    class FormUrlEncodedContentTypeConvert implements ContentTypeConvert {
        @Override
        public String getContentType() {
            return MediaTypes.APPLICATION_WWW_FORM;
        }

        @Override
        public FormUrlBody getBody(StandardHttpRequestParam standardHttpRequestParam) {
            Body originBody = standardHttpRequestParam.getBody();
            if (!(originBody instanceof FormUrlBody) || originBody == null)
                return new FormUrlBody(urlencodedRequestBodyPage.getTableMap(RowDataState.available));
            ((FormUrlBody) originBody).getData().addAll(urlencodedRequestBodyPage.getTableMap(RowDataState.available));
            return ((FormUrlBody) originBody);

        }
    }

    class XmlContentTypeConvert implements ContentTypeConvert {
        @Override
        public String getContentType() {
            return MediaTypes.APPLICATION_XML;
        }

        @Override
        public XMLBody getBody(StandardHttpRequestParam standardHttpRequestParam) {
            return new XMLBody(xmlParamRequestBodyPage.getText());
        }
    }

    class BinaryContentTypeConvert implements ContentTypeConvert {
        @Override
        public String getContentType() {
            return MediaTypes.APPLICATION_STREAM;
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

        @Override
        public String getContentType() {
            return MediaTypes.TEXT;
        }
    }

    class FormDataContentTypeConvert implements ContentTypeConvert {
        @Override
        public String getContentType() {
            return MediaTypes.MULTIPART_FORM_DATA;
        }

        @Override
        public FormBody getBody(StandardHttpRequestParam standardHttpRequestParam) {
            Body body = standardHttpRequestParam.getBody();
            if (!(body instanceof FormBody))
                return new FormBody(formDataRequestBodyPage.getFormData(RowDataState.available));
            ((FormBody) body).getData().addAll(formDataRequestBodyPage.getFormData(RowDataState.available));
            return ((FormBody) body);
        }
    }

    static class NoneDataContentTypeConvert implements ContentTypeConvert {
        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public EmptyBody getBody(StandardHttpRequestParam standardHttpRequestParam) {
            return new EmptyBody();
        }
    }
}
