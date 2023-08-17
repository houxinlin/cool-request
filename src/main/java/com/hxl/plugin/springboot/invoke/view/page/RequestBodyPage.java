package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;
import com.hxl.plugin.springboot.invoke.net.FormDataInfo;
import com.hxl.plugin.springboot.invoke.net.MapRequest;
import com.hxl.plugin.springboot.invoke.utils.UrlUtils;
import com.intellij.openapi.project.Project;
import com.intellij.util.Consumer;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class RequestBodyPage extends JPanel implements MapRequest {
    private static final Map<String, ContentTypeConvert> CONTENT_TYPE_MAP = new HashMap<>();
    private final Project project;
    private final Map<String, JRadioButton> radioButtons = new HashMap<>();
    private JSONRequestBodyPage jsonRequestBodyPage;
    private XmlParamRequestBodyPage xmlParamRequestBodyPage;
    private RawParamRequestBodyPage rawParamRequestBodyPage;
    private BinaryRequestBodyPage binaryRequestBodyPage;
    private FormDataRequestBodyPage formDataRequestBodyPage;
    private FormUrlencodedRequestBodyPage formUrlencodedRequestBodyPage;

    {
        CONTENT_TYPE_MAP.put("form-data", new FormDataContentTypeConvert());
        CONTENT_TYPE_MAP.put("x-www-form-urlencoded", new FormUrlEncodedContentTypeConvert());
        CONTENT_TYPE_MAP.put("json", new ApplicationJSONContentTypeConvert());
        CONTENT_TYPE_MAP.put("xml", new XmlContentTypeConvert());
        CONTENT_TYPE_MAP.put("raw", new RawContentTypeConvert());
    }


    public RequestBodyPage(Project project) {
        this.project = project;
        init();
    }

    private JRadioButton createJRadioButton(String name, Consumer<String> callable) {
        JRadioButton jRadioButton = new JRadioButton(name);
        jRadioButton.addActionListener(e -> callable.consume(name));
        return jRadioButton;
    }

    @Override
    public void configRequest(ControllerInvoke.ControllerRequestData controllerRequestData) {
        //设置content-type
        String selectType = "";
        for (String key : radioButtons.keySet()) {
            if (radioButtons.get(key).isSelected()) {
                selectType = key;
                ContentTypeConvert contentTypeConvert = CONTENT_TYPE_MAP.get(key);
                controllerRequestData.setContentType(contentTypeConvert.getContentType());
                break;
            }
        }
        controllerRequestData.setBody(CONTENT_TYPE_MAP.getOrDefault(selectType, new ContentTypeConvert() {
            @Override
            public String getContentType() {
                return "text/paint";
            }

            @Override
            public String getBody(ControllerInvoke.ControllerRequestData controllerRequestData) {
                return "";
            }
        }).getBody());
//        radioButtons.get(selectType)
//        if (radioButtons.get("json").isSelected()){
//            controllerRequestData.setBody(jsonRequestBodyPage.getText());
//        }
//        if (radioButtons.get("x-www-form-urlencoded").isSelected()){
//
//            formUrlencodedRequestBodyPage.foreach((s, s2) -> {
//
//            });
//        }
//        if ()
    }

    public void init() {
        setLayout(new BorderLayout());
        jsonRequestBodyPage = new JSONRequestBodyPage(this.project);
        xmlParamRequestBodyPage = new XmlParamRequestBodyPage(this.project);
        rawParamRequestBodyPage = new RawParamRequestBodyPage(this.project);
        binaryRequestBodyPage = new BinaryRequestBodyPage(this.project);
        formUrlencodedRequestBodyPage = new FormUrlencodedRequestBodyPage();
        formDataRequestBodyPage= new FormDataRequestBodyPage();
        Map<String, JPanel> pageMap = new HashMap<>();

        List<String> sortParam = Arrays.asList("form-data", "x-www-form-urlencoded", "json", "xml", "raw", "binary");

        pageMap.put("form-data", formDataRequestBodyPage);
        pageMap.put("x-www-form-urlencoded", formUrlencodedRequestBodyPage);
        pageMap.put("json", jsonRequestBodyPage);
        pageMap.put("xml", xmlParamRequestBodyPage);
        pageMap.put("raw", rawParamRequestBodyPage);
        pageMap.put("binary", binaryRequestBodyPage);

        JPanel topJPanel = new JPanel();
        JPanel contentPageJPanel = new JPanel();
        CardLayout cardLayout = new CardLayout();

        contentPageJPanel.setLayout(cardLayout);


        ButtonGroup buttonGroup = new ButtonGroup();
        Consumer<String> radioButtonConsumer = s -> {
            cardLayout.show(contentPageJPanel, s);
        };
        for (String paramName : sortParam) {
            JRadioButton jRadioButton = createJRadioButton(paramName, radioButtonConsumer);
            radioButtons.put(paramName, jRadioButton);
            buttonGroup.add(jRadioButton);
            topJPanel.add(jRadioButton);
            contentPageJPanel.add(paramName, pageMap.get(paramName));
        }
        radioButtons.get("json").setSelected(true);

        topJPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        add(topJPanel, BorderLayout.NORTH);
        add(contentPageJPanel, BorderLayout.CENTER);
    }

    interface ContentTypeConvert {
        public String getContentType();

        public String getBody(ControllerInvoke.ControllerRequestData controllerRequestData);
    }

    class ApplicationJSONContentTypeConvert implements ContentTypeConvert {
        @Override
        public String getContentType() {
            return "application/json";
        }

        @Override
        public String getBody(ControllerInvoke.ControllerRequestData controllerRequestData) {
            return jsonRequestBodyPage.getText();
        }
    }

    class FormUrlEncodedContentTypeConvert implements ContentTypeConvert {
        @Override
        public String getContentType() {
            return "application/x-www-form-urlencoded";
        }

        @Override
        public String getBody(ControllerInvoke.ControllerRequestData controllerRequestData) {
            return UrlUtils.mapToUrlParams(formUrlencodedRequestBodyPage.getTableMap());

        }
    }

    class XmlContentTypeConvert implements ContentTypeConvert {
        @Override
        public String getContentType() {
            return "application/xml";
        }

        @Override
        public String getBody(ControllerInvoke.ControllerRequestData controllerRequestData) {
            return xmlParamRequestBodyPage.getText();
        }
    }

    class RawContentTypeConvert implements ContentTypeConvert {
        @Override
        public String getContentType() {
            return "text/paint";
        }

        @Override
        public String getBody(ControllerInvoke.ControllerRequestData controllerRequestData) {
            return rawParamRequestBodyPage.getText();
        }
    }
    class FormDataContentTypeConvert implements ContentTypeConvert{
        @Override
        public String getContentType() {
            return "multipart/form-data";
        }

        @Override
        public String getBody(ControllerInvoke.ControllerRequestData controllerRequestData) {
            List<FormDataInfo> formDataInfos = new ArrayList<>();
            formDataRequestBodyPage.toMap().forEach(item -> {
                String name = item.get("key");
                String value = item.get("value");
                String type = item.get("type");
                formDataInfos.add(new FormDataInfo(name, value, type));
            });
            controllerRequestData.setFormData(formDataInfos);
            return "";
        }
    }
}
