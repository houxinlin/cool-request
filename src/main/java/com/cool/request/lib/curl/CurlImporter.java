/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CurlImporter.java is part of Cool Request
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

package com.cool.request.lib.curl;

import com.cool.request.components.http.FormDataInfo;
import com.cool.request.components.http.KeyValue;
import com.cool.request.components.http.net.*;
import com.cool.request.utils.MediaTypeUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.utils.UrlUtils;
import com.cool.request.view.main.IRequestParamManager;

import java.util.List;
import java.util.stream.Collectors;

public class CurlImporter {
    public static void doImport(String curl, IRequestParamManager iRequestParamManager) {
        if (!StringUtils.hasText(curl)) return;
        BasicCurlParser.Request parse = new BasicCurlParser().parse(curl);
        if (StringUtils.isEmpty(parse.getUrl())) return;
        iRequestParamManager.restParam();
        iRequestParamManager.setHttpMethod(HttpMethod.parse(parse.getMethod()));
        List<KeyValue> header = parse.getHeaders()
                .stream()
                .map(pair -> new KeyValue(StringUtils.headerNormalized(pair.getKey()), pair.getValue())).collect(Collectors.toList());
        //设置请求头
        iRequestParamManager.setHttpHeader(header);
        String contentType = iRequestParamManager.getContentTypeFromHeader();

        iRequestParamManager.setUrl(parse.getUrl());
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
