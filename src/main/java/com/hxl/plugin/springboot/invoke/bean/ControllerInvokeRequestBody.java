package com.hxl.plugin.springboot.invoke.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;

public class ControllerInvokeRequestBody {
    private final String type="controller";
    private String id;
    private String url;
    private String body;
    private String contentType;

    private boolean useProxyObject;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        try {
            return ObjectMappingUtils.getInstance().writeValueAsString(this);
        } catch (JsonProcessingException ignored) {

        }
       return "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isUseProxyObject() {
        return useProxyObject;
    }

    public void setUseProxyObject(boolean useProxyObject) {
        this.useProxyObject = useProxyObject;
    }


    public static final class ControllerInvokeRequestBodyBuilder {
        private String type;
        private String id;
        private String url;
        private String body;
        private String contentType;
        private boolean useProxyObject;

        private ControllerInvokeRequestBodyBuilder() {
        }

        public static ControllerInvokeRequestBodyBuilder aControllerInvokeRequestBody() {
            return new ControllerInvokeRequestBodyBuilder();
        }

        public ControllerInvokeRequestBodyBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public ControllerInvokeRequestBodyBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public ControllerInvokeRequestBodyBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public ControllerInvokeRequestBodyBuilder withBody(String body) {
            this.body = body;
            return this;
        }

        public ControllerInvokeRequestBodyBuilder withContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public ControllerInvokeRequestBodyBuilder withUseProxyObject(boolean useProxyObject) {
            this.useProxyObject = useProxyObject;
            return this;
        }

        public ControllerInvokeRequestBody build() {
            ControllerInvokeRequestBody controllerInvokeRequestBody = new ControllerInvokeRequestBody();
            controllerInvokeRequestBody.setId(id);
            controllerInvokeRequestBody.setUrl(url);
            controllerInvokeRequestBody.setBody(body);
            controllerInvokeRequestBody.setContentType(contentType);
            controllerInvokeRequestBody.setUseProxyObject(useProxyObject);
            return controllerInvokeRequestBody;
        }
    }
}
