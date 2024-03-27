/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * BaseSetting.java is part of Cool Request
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

package com.cool.request.view.dialog;

public class BaseSetting {
    private  int languageIndex;
    private boolean enableDynamicRefresh;
    private boolean autoRefreshData;
    private boolean listenerGateway;
    private boolean autoNavigation;
    private boolean mergeApiAndRequest;
    private String proxyIp;
    private int proxyPort;
    private boolean parameterCoverage;

    public String getProxyIp() {
        return proxyIp;
    }

    public void setProxyIp(String proxyIp) {
        this.proxyIp = proxyIp;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public boolean isParameterCoverage() {
        return parameterCoverage;
    }

    public void setParameterCoverage(boolean parameterCoverage) {
        this.parameterCoverage = parameterCoverage;
    }

    public boolean isMergeApiAndRequest() {
        return mergeApiAndRequest;
    }

    public void setMergeApiAndRequest(boolean mergeApiAndRequest) {
        this.mergeApiAndRequest = mergeApiAndRequest;
    }

    public boolean isEnableDynamicRefresh() {
        return enableDynamicRefresh;
    }

    public void setEnableDynamicRefresh(boolean enableDynamicRefresh) {
        this.enableDynamicRefresh = enableDynamicRefresh;
    }

    public boolean isAutoRefreshData() {
        return autoRefreshData;
    }

    public void setAutoRefreshData(boolean autoRefreshData) {
        this.autoRefreshData = autoRefreshData;
    }

    public boolean isListenerGateway() {
        return listenerGateway;
    }

    public void setListenerGateway(boolean listenerGateway) {
        this.listenerGateway = listenerGateway;
    }

    public boolean isAutoNavigation() {
        return autoNavigation;
    }

    public void setAutoNavigation(boolean autoNavigation) {
        this.autoNavigation = autoNavigation;
    }

    public BaseSetting(int languageIndex) {
        this.languageIndex = languageIndex;
    }


    public BaseSetting() {
    }

    public int getLanguageIndex() {
        return languageIndex;
    }

    public void setLanguageIndex(int languageIndex) {
        this.languageIndex = languageIndex;
    }
}
