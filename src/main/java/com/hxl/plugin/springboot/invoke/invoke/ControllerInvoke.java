package com.hxl.plugin.springboot.invoke.invoke;

import com.hxl.plugin.springboot.invoke.bean.ControllerInvokeRequestBody;

public class ControllerInvoke extends BaseProjectInvoke<ControllerInvoke.InvokeData> {

    public ControllerInvoke(int port) {
        super(port);
    }

    @Override
    public String createMessage(InvokeData invokeData) {
        return ControllerInvokeRequestBody.ControllerInvokeRequestBodyBuilder.aControllerInvokeRequestBody()
                .withUseProxyObject(invokeData.useProxyObject)
                .withUrl(invokeData.getUrl())
                .withId(invokeData.getId())
                .withBody(invokeData.requestBody)
                .withContentType(invokeData.getContentType())
                .build().toString();
    }
    public static class InvokeData {
        private String url;
        private String contentType;
        private String requestBody;
        private String id;
        private boolean useProxyObject;

        public boolean isUseProxyObject() {
            return useProxyObject;
        }

        public void setUseProxyObject(boolean useProxyObject) {
            this.useProxyObject = useProxyObject;
        }

        public InvokeData(String url, String contentType, String requestBody, String id, boolean useProxyObject) {
            this.url = url;
            this.contentType = contentType;
            this.requestBody = requestBody;
            this.id = id;
            this.useProxyObject =useProxyObject;
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

        public String getRequestBody() {
            return requestBody;
        }

        public void setRequestBody(String requestBody) {
            this.requestBody = requestBody;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
