package com.cool.request.utils.param;

import com.cool.request.common.bean.EmptyEnvironment;
import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.components.http.Controller;
import com.cool.request.components.http.RequestParameterDescription;
import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.components.http.KeyValue;
import com.cool.request.lib.springmvc.*;
import com.cool.request.utils.CollectionUtils;
import com.cool.request.utils.ControllerUtils;
import com.cool.request.utils.StringUtils;
import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GuessParameterProvider implements HTTPParameterProvider {
    private HttpRequestInfo getHttpRequestInfo(Project project, Controller controller) {
        HttpRequestInfo httpRequestInfo = null;
        if (SwingUtilities.isEventDispatchThread()) {
            httpRequestInfo = new SpringMvcRequestMapping().getHttpRequestInfo(project, controller);
        } else {
            httpRequestInfo = ApplicationManager
                    .getApplication()
                    .runReadAction((Computable<HttpRequestInfo>) () -> new SpringMvcRequestMapping().getHttpRequestInfo(project, controller));
        }
        return httpRequestInfo;
    }

    @Override
    public List<KeyValue> getHeader(Project project, Controller controller, RequestEnvironment environment) {

        List<KeyValue> guessHeader = getHttpRequestInfo(project, controller).getHeaders().stream()
                .map(requestParameterDescription -> new KeyValue(requestParameterDescription.getName(), "")).collect(Collectors.toList());
        return CollectionUtils.merge(guessHeader, environment.getHeader());
    }

    @Override
    public List<KeyValue> getUrlParam(Project project, Controller controller, RequestEnvironment environment) {
        HttpRequestInfo httpRequestInfo = getHttpRequestInfo(project, controller);

        List<KeyValue> guessParam = httpRequestInfo.getUrlParams().stream()
                .map(requestParameterDescription -> new KeyValue(requestParameterDescription.getName(), "", requestParameterDescription.getType())).collect(Collectors.toList());
        return CollectionUtils.merge(guessParam, environment.getUrlParam());
    }

    @Override
    public Body getBody(Project project, Controller controller, RequestEnvironment environment) {
        HttpRequestInfo httpRequestInfo = getHttpRequestInfo(project, controller);
        GuessBody requestBody = httpRequestInfo.getRequestBody();
        //目前参数推测只支持两种String和JSON
        if (requestBody instanceof StringGuessBody) {
            return new StringBody(((StringGuessBody) requestBody).getValue());
        }

        if (requestBody instanceof JSONObjectGuessBody) {
            Map<String, Object> json = ((JSONObjectGuessBody) requestBody).getJson();
            if (json != null) {
                return new JSONBody(new Gson().toJson(((JSONObjectGuessBody) requestBody).getJson()));
            }
        }
        if (httpRequestInfo.getFormDataInfos() != null && !httpRequestInfo.getFormDataInfos().isEmpty()) {
            return new FormBody(httpRequestInfo.getFormDataInfos());
        }

        List<RequestParameterDescription> urlencodedBody = httpRequestInfo.getUrlencodedBody();
        if (urlencodedBody != null && !urlencodedBody.isEmpty()) {
            List<KeyValue> keyValues = urlencodedBody.stream()
                    .map(requestParameterDescription ->
                            new KeyValue(requestParameterDescription.getName(), "")).collect(Collectors.toList());
            return new FormUrlBody(CollectionUtils.merge(keyValues, environment.getFormUrlencoded()));
        }


        return new EmptyBody();
    }

    @Override
    public String getFullUrl(Project project, Controller controller, RequestEnvironment environment) {
        if (environment instanceof EmptyEnvironment) {
            return ControllerUtils.buildLocalhostUrl(controller);
        }
        return StringUtils.joinUrlPath(environment.getHostAddress(), controller.getContextPath(), controller.getUrl());
    }

    @Override
    public HttpMethod getHttpMethod(Project project, Controller controller, RequestEnvironment environment) {
        return HttpMethod.parse(controller.getHttpMethod());
    }
}
