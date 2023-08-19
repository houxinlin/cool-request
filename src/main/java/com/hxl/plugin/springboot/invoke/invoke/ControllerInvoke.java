package com.hxl.plugin.springboot.invoke.invoke;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hxl.plugin.springboot.invoke.net.FormDataInfo;
import com.hxl.plugin.springboot.invoke.net.KeyValue;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;

import java.util.ArrayList;
import java.util.List;

public class ControllerInvoke extends BasicRemoteInvoke<ControllerInvoke.ControllerRequestData> {
    public ControllerInvoke(int port) {
        super(port);
    }
    @Override
    public String createMessage(ControllerRequestData controllerRequestData) {
        try {
            return ObjectMappingUtils.getInstance().writeValueAsString(controllerRequestData);
        } catch (JsonProcessingException ignored) {
        }
        return "";
    }

    public static class ControllerRequestData {
        private final String type = "controller";
        private String url;  //url,可设置
        private String contentType;
        private List<FormDataInfo> formData =new ArrayList<>();
        private String body; //json xml raw bin urlencoded
        private String id;
        private boolean useProxyObject;
        private boolean useInterceptor;
        private boolean userFilter;
        private List<KeyValue> headers =new ArrayList<>();
        private String method;

        public void setFormData(List<FormDataInfo> formData) {
            this.formData = formData;
        }

        public String getMethod() {
            return method;
        }

        public List<FormDataInfo> getFormData() {
            return formData;
        }


        public void setMethod(String method) {
            this.method = method;
        }

        public List<KeyValue> getHeaders() {
            return headers;
        }

        public void addHeader(String key, String value){
            getHeaders().add(new KeyValue(key,value));
        }
        public void setHeader(String key, String value){
            getHeaders().removeIf(keyValue -> keyValue.getKey().equalsIgnoreCase(key));
            addHeader(key,value);
        }
        public String getType() {
            return type;
        }

        public boolean isUseInterceptor() {
            return useInterceptor;
        }

        public void setUseInterceptor(boolean useInterceptor) {
            this.useInterceptor = useInterceptor;
        }

        public boolean isUserFilter() {
            return userFilter;
        }

        public void setUserFilter(boolean userFilter) {
            this.userFilter = userFilter;
        }

        public boolean isUseProxyObject() {
            return useProxyObject;
        }

        public void setUseProxyObject(boolean useProxyObject) {
            this.useProxyObject = useProxyObject;
        }

        public ControllerRequestData(String method, String url,
                                     String id, boolean useProxyObject, boolean useInterceptor, boolean userFilter) {
            this.method = method;
            this.url = url;
            this.id = id;
            this.useProxyObject = useProxyObject;
            this.useInterceptor = useInterceptor;
            this.userFilter = userFilter;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }


        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
