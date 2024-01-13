package com.hxl.plugin.springboot.invoke.bean;

import java.util.Objects;

/**
 * 请求环境，可手动增加、网关发现
 */
public class RequestEnvironment {
    private String environmentName;

    private String hostAddress;

    private String id;

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
