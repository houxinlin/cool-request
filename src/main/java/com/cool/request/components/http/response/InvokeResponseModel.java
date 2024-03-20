package com.cool.request.components.http.response;

import com.cool.request.components.http.Header;

import java.io.Serializable;
import java.util.List;

public class InvokeResponseModel implements Serializable {
    private static final long serialVersionUID = 1000000;
    private List<Header> header;
    private String baseBodyData;
    private String id;
    private int code = -1;
    private Object attachData;

    public Object getAttachData() {
        return attachData;
    }

    public void setAttachData(Object attachData) {
        this.attachData = attachData;
    }

    public String headerToString() {
        StringBuilder headerStringBuffer = new StringBuilder();
        for (Header header : getHeader()) {
            headerStringBuffer.append(header.getKey()).append(": ").append(header.getValue());
            headerStringBuffer.append("\n");
        }
        return headerStringBuffer.toString();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Header> getHeader() {
        return header;
    }

    public void setHeader(List<Header> header) {
        this.header = header;
    }

    public String getBaseBodyData() {
        return baseBodyData;
    }

    public void setBaseBodyData(String baseBodyData) {
        this.baseBodyData = baseBodyData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
