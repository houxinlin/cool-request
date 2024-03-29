/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * IEnvImpl.java is part of Cool Request
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

package com.cool.request.components.http.script;

import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.state.CoolRequestEnvironmentPersistentComponent;
import com.cool.request.components.http.FormDataInfo;
import com.cool.request.components.http.KeyValue;
import com.cool.request.script.Env;
import com.cool.request.script.Form;
import com.cool.request.script.IEnv;
import com.cool.request.utils.RequestParamUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.tool.provider.RequestEnvironmentProvideImpl;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IEnvImpl implements IEnv {
    private Project project;

    public IEnvImpl(Project project) {
        this.project = project;
    }

    @Override
    public void createNewEnv(String name) {
        if (StringUtils.isBlank(name)) return;
        if (!getEnvsByName(name).isEmpty()) return;
        CoolRequestEnvironmentPersistentComponent.getInstance(project).addNewEnv(name);
    }

    @Override
    public Env getEnvByName(String envName) {
        CoolRequestEnvironmentPersistentComponent.State env = CoolRequestEnvironmentPersistentComponent.getInstance(project);
        for (RequestEnvironment environment : env.getEnvironments()) {
            if (StringUtils.isEquals(environment.getEnvironmentName(), envName)) {
                return new EnvImpl(environment);
            }
        }
        return null;
    }

    @Override
    public List<Env> getEnvsByName(String envName) {
        return CoolRequestEnvironmentPersistentComponent.getInstance(project)
                .getEnvironments().stream()
                .filter(requestEnvironment -> StringUtils.isEquals(requestEnvironment.getEnvironmentName(), envName))
                .collect(Collectors.toList()).stream()
                .map(requestEnvironment ->
                        new EnvImpl(requestEnvironment)).collect(Collectors.toList());

    }

    @Override
    public Env getCurrentEnv() {
        return new EnvImpl(RequestEnvironmentProvideImpl.getInstance(project).getSelectRequestEnvironment());
    }

    class EnvImpl implements Env {
        private RequestEnvironment selectRequestEnvironment;

        public EnvImpl(RequestEnvironment environment) {
            this.selectRequestEnvironment = environment;
        }

        @Override
        public void removeUrlParam(String key) {
            selectRequestEnvironment.getUrlParam().removeIf(keyValue -> StringUtils.isEquals(key, keyValue.getKey()));
        }

        @Override
        public void removeHeader(String key) {
            selectRequestEnvironment.getHeader().removeIf(keyValue -> StringUtils.isEquals(key, keyValue.getKey()));
        }

        @Override
        public void removeForm(String key) {
            selectRequestEnvironment.getFormData().removeIf(keyValue -> StringUtils.isEquals(key, keyValue.getName()));
        }

        @Override
        public void removeFormUrlencoded(String key) {
            selectRequestEnvironment.getFormUrlencoded().removeIf(keyValue -> StringUtils.isEquals(key, keyValue.getKey()));
        }

        @Override
        public List<String> listHeaderKeys() {
            return selectRequestEnvironment.getHeader().stream().map(keyValue -> keyValue.getKey()).collect(Collectors.toList());
        }

        @Override
        public List<String> listUrlParamKeys() {
            return selectRequestEnvironment.getUrlParam().stream().map(keyValue -> keyValue.getKey()).collect(Collectors.toList());
        }

        @Override
        public List<String> listFormKeys() {
            return selectRequestEnvironment.getFormData().stream().map(keyValue -> keyValue.getName()).collect(Collectors.toList());
        }

        @Override
        public List<String> listFormUrlencodedKeys() {
            return selectRequestEnvironment.getFormUrlencoded().stream().map(keyValue -> keyValue.getKey()).collect(Collectors.toList());
        }

        @Override
        public String getEnvName() {
            return selectRequestEnvironment.getEnvironmentName();
        }

        @Override
        public String getEnvUrlParam(String key) {
            return RequestParamUtils.getFirstValue(selectRequestEnvironment.getUrlParam(), key);
        }

        @Override
        public List<String> getEnvUrlParams(String key) {
            return RequestParamUtils.getValues(selectRequestEnvironment.getUrlParam(), key);
        }

        @Override
        public String getEnvHeader(String key) {
            return RequestParamUtils.getFirstValue(selectRequestEnvironment.getHeader(), key);
        }

        @Override
        public List<String> getEnvHeaders(String key) {
            return RequestParamUtils.getValues(selectRequestEnvironment.getHeader(), key);
        }

        @Override
        public String getEnvFormUrlencoded(String key) {
            return RequestParamUtils.getFirstValue(selectRequestEnvironment.getFormUrlencoded(), key);
        }

        @Override
        public List<String> getEnvFormUrlencodeds(String key) {
            return RequestParamUtils.getValues(selectRequestEnvironment.getFormUrlencoded(), key);
        }

        @Override
        public Form getEnvForm(String key) {
            for (FormDataInfo formDatum : selectRequestEnvironment.getFormData()) {
                if (StringUtils.isEquals(formDatum.getName(), key)) {
                    Form form = new Form();
                    form.setFile(formDatum.getType().equals("file"));
                    form.setValue(formDatum.getValue());
                    form.setName(formDatum.getName());
                    return form;
                }
            }
            return null;
        }

        @Override
        public List<Form> getEnvForms(String key) {
            List<Form> result = new ArrayList<>();
            for (FormDataInfo formDatum : selectRequestEnvironment.getFormData()) {
                if (StringUtils.isEquals(formDatum.getName(), key)) {
                    Form form = new Form();
                    form.setFile(formDatum.getType().equals("file"));
                    form.setValue(formDatum.getValue());
                    form.setName(formDatum.getName());
                    result.add(form);
                }
            }
            return result;
        }

        @Override
        public void setEnvUrlParam(String key, String value) {
            removeUrlParam(key);
            selectRequestEnvironment.getUrlParam().add(new KeyValue(key, value));
        }

        @Override
        public void setEnvHeader(String key, String value) {
            removeHeader(key);
            selectRequestEnvironment.getHeader().add(new KeyValue(key, value));
        }

        @Override
        public void setEnvForm(String key, String value, boolean isFile) {
            removeForm(key);
            selectRequestEnvironment.getFormData().add(new FormDataInfo(key, value, isFile ? "file" : "text"));
        }

        @Override
        public void setEnvFormUrlencoded(String key, String value) {
            removeFormUrlencoded(key);
            selectRequestEnvironment.getFormUrlencoded().add(new KeyValue(key, value));
        }
    }
}
