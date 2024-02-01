package com.cool.request.lib.curl;

import com.cool.request.component.http.net.*;
import com.cool.request.utils.MediaTypeUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.utils.UrlUtils;
import com.cool.request.view.main.IRequestParamManager;

import java.util.List;
import java.util.stream.Collectors;

public class CurlImporter {
    public static void doImport(String curl, IRequestParamManager iRequestParamManager) {
        BasicCurlParser.Request parse = new BasicCurlParser().parse(curl);
        iRequestParamManager.restParam();
        iRequestParamManager.setHttpMethod(HttpMethod.parse(parse.getMethod()));
        List<KeyValue> header = parse.getHeaders()
                .stream()
                .map(pair -> new KeyValue(StringUtils.headerNormalized(pair.getKey()), pair.getValue())).collect(Collectors.toList());
        //设置请求头
        iRequestParamManager.setHttpHeader(header);
        String contentType = iRequestParamManager.getContentTypeFromHeader();


        List<FormDataInfo> formDataInfos = parse.getFormData().stream().map(stringArgumentHolderPair -> {
            ArgumentHolder argumentHolder = stringArgumentHolderPair.getValue();
            String value = "";
            value = argumentHolder.getName();
            return new FormDataInfo(stringArgumentHolderPair.getKey(), value, argumentHolder instanceof StringArgumentHolder ? "text" : "file");
        }).collect(Collectors.toList());

        //1.如果依靠解析库解析到了form data数据，则直接设置为form data数据
        if (!formDataInfos.isEmpty()) {
            iRequestParamManager.setFormData(formDataInfos);
            iRequestParamManager.switchRequestBodyType(new MediaType(MediaTypes.MULTIPART_FORM_DATA)); //剩余的类型都设置为raw文本类型
            return;
        }
        //2.如果contentType在空的情况下，解析推测post data是json格式，则设置为json数据
        if (StringUtils.isEmpty(contentType) &&
                !StringUtils.isEmpty(parse.getPostData()) &&
                StringUtils.isValidJson(parse.getPostData())) {
            contentType = MediaTypes.APPLICATION_JSON;
        }

        if (StringUtils.isEmpty(contentType)) contentType = MediaTypes.TEXT;
        //4. 如果解析推测post data是x-www-form-urlencoded格式，则设置为x-www-form-urlencoded数据

        //根据contentType的不同，设置不同数据
        if (MediaTypeUtils.isFormUrlencoded(contentType)) {
            String postData = parse.getPostData();
            List<KeyValue> keyValues = UrlUtils.parseFormData(postData);
            iRequestParamManager.setUrlencodedBody(keyValues);//x-www-form-urlencoded
            iRequestParamManager.switchRequestBodyType(new MediaType(MediaTypes.APPLICATION_WWW_FORM));
        } else if (MediaTypeUtils.isJson(contentType) || MediaTypeUtils.isXml(contentType)) {
            iRequestParamManager.setRequestBody(contentType, parse.getPostData());//json xml
        } else if (MediaTypeUtils.isFormData(contentType)) {
            List<FormDataInfo> formValues = UrlUtils.parseFormData(parse.getPostData()).stream()
                    .map(keyValue -> new FormDataInfo(keyValue.getKey(),
                            keyValue.getValue(), "text")).collect(Collectors.toList());
            iRequestParamManager.setFormData(formValues);//x-www-form-urlencoded
            iRequestParamManager.switchRequestBodyType(new MediaType(MediaTypes.MULTIPART_FORM_DATA));
        } else {
            iRequestParamManager.setRequestBody(MediaTypes.TEXT, parse.getPostData());//剩余的类型都设置为raw文本类型
        }
    }
}
