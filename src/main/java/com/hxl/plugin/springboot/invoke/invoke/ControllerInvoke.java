package com.hxl.plugin.springboot.invoke.invoke;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;

import java.util.Map;

public class ControllerInvoke extends BaseProjectInvoke<ControllerInvoke.InvokeData> {

    public ControllerInvoke(int port) {
        super(port);
    }

    @Override
    public String createMessage(InvokeData invokeData) {
        try {
            return ObjectMappingUtils.getInstance().writeValueAsString(invokeData);
        } catch (JsonProcessingException e) {

        }
        return "";
//        return ControllerInvokeRequestBody.ControllerInvokeRequestBodyBuilder.aControllerInvokeRequestBody()
//                .withUseProxyObject(invokeData.useProxyObject)
//                .withUrl(invokeData.getUrl())
//                .withId(invokeData.getId())
//                .withBody(invokeData.requestBody)
//                .withContentType(invokeData.getContentType())
//                .build().toString();
    }

    public static class InvokeData {
        private final String type = "controller";
        private String url;
        private String contentType;
        private String body;
        private String id;
        private boolean useProxyObject;
        private boolean useInterceptor;
        private boolean userFilter;
        private Map<String, Object> headers;
        private String method;

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public Map<String, Object> getHeaders() {
            return headers;
        }
        public void setHeaders(Map<String, Object> headers) {
            this.headers = headers;
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

        public InvokeData(String method, String url, String contentType, String body,
                          String id, boolean useProxyObject, boolean useInterceptor, boolean userFilter, Map<String, Object> headers) {
            this.method = method;
            this.url = url;
            this.contentType = contentType;
            this.body = body;
            this.id = id;
            this.useProxyObject = useProxyObject;
            this.useInterceptor = useInterceptor;
            this.userFilter = userFilter;
            this.headers = headers;
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
