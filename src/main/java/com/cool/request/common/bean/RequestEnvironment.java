/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * RequestEnvironment.java is part of Cool Request
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

package com.cool.request.common.bean;

import com.cool.request.components.http.FormDataInfo;
import com.cool.request.components.http.KeyValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 请求环境，可手动增加、网关发现
 */
public class RequestEnvironment implements Cloneable {
    private String environmentName;
    private String hostAddress;
    private String id;
    private List<KeyValue> header = new ArrayList<>();
    private List<KeyValue> urlParam = new ArrayList<>();
    private List<FormDataInfo> formData = new ArrayList<>();
    private List<KeyValue> formUrlencoded = new ArrayList<>();

    public List<KeyValue> getHeader() {
        return header;
    }

    public void setHeader(List<KeyValue> header) {
        this.header = header;
    }

    public List<KeyValue> getUrlParam() {
        return urlParam;
    }

    public void setUrlParam(List<KeyValue> urlParam) {
        this.urlParam = urlParam;
    }

    public List<FormDataInfo> getFormData() {
        return formData;
    }

    public void setFormData(List<FormDataInfo> formData) {
        this.formData = formData;
    }

    public List<KeyValue> getFormUrlencoded() {
        return formUrlencoded;
    }

    public void setFormUrlencoded(List<KeyValue> formUrlencoded) {
        this.formUrlencoded = formUrlencoded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    @Override
    public RequestEnvironment clone() {
        RequestEnvironment cloned = new RequestEnvironment();

        cloned.setId(getId());
        cloned.setEnvironmentName(getEnvironmentName());
        cloned.setHostAddress(getHostAddress());

        cloned.header = new ArrayList<>(header.size());
        for (KeyValue keyValue : header) {
            cloned.header.add(keyValue.clone());
        }
        cloned.urlParam = new ArrayList<>(urlParam.size());
        for (KeyValue keyValue : urlParam) {
            cloned.urlParam.add(keyValue.clone());
        }
        cloned.formData = new ArrayList<>(formData.size());
        for (FormDataInfo formDataInfo : formData) {
            cloned.formData.add(formDataInfo.clone());
        }
        cloned.formUrlencoded = new ArrayList<>(formUrlencoded.size());
        for (KeyValue keyValue : formUrlencoded) {
            cloned.formUrlencoded.add(keyValue.clone());
        }
        return cloned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestEnvironment that = (RequestEnvironment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public String toString() {
        return getHostAddress();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
