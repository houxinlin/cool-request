package com.hxl.plugin.springboot.invoke.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.model.SpringMvcRequestMappingInvokeBean;
import com.hxl.plugin.springboot.invoke.plugin.apifox.ApiFoxExport;
import com.hxl.plugin.springboot.invoke.springmvc.*;
import com.hxl.plugin.springboot.invoke.utils.PsiUtils;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.hxl.utils.openapi.OpenApi;
import com.hxl.utils.openapi.OpenApiBuilder;
import com.hxl.utils.openapi.Type;
import com.hxl.utils.openapi.parameter.OpenApiUrlQueryParameter;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiClass;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.ui.tree.TreeUtil;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApifoxExportAnAction extends AnAction {
    private final SimpleTree simpleTree;
    private final ApiFoxExport apifoxExp = new ApiFoxExport();

    public ApifoxExportAnAction(SimpleTree simpleTree) {
        super("apifox", "apifox", MyIcons.APIFOX);
        this.simpleTree = simpleTree;
    }

    private List<MainTopTreeView.RequestMappingNode> mapToMappingNode(List<Object> objects) {

        return new ArrayList<>();
    }

    private String toOpenApiJson(List<RequestMappingModel> requestMappingModelList) {
        OpenApi openApi = new OpenApi();
        for (RequestMappingModel requestMappingModel : requestMappingModelList) {
            SpringMvcRequestMappingInvokeBean controller = requestMappingModel.getController();
            HttpRequestInfo httpRequestInfo = SpringMvcRequestMappingUtils.getHttpRequestInfo(requestMappingModel);
            MethodDescription methodDescription = ParameterAnnotationDescriptionUtils.getMethodDescription(PsiUtils.findMethod(controller.getSimpleClassName(), controller.getMethodName()));

            OpenApiBuilder openApiBuilder = OpenApiBuilder.get(controller.getUrl(), Optional.ofNullable(methodDescription.getSummary()).orElse(controller.getMethodName()));
            for (RequestParameterDescription urlParam : httpRequestInfo.getUrlParams()) {
                openApiBuilder.addParameter(
                        new OpenApiUrlQueryParameter(urlParam.getName(), urlParam.getDescription(), true, Type.parse(urlParam.getName(), Type.string)));
            }
            openApiBuilder.addToOpenApi(openApi);
        }
        try {
            return new ObjectMapper().writeValueAsString(openApi);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (!apifoxExp.canExport()) {
            apifoxExp.showCondition();
            return;
        }
        List<RequestMappingModel> requestMappingModels = new ArrayList<>();
        List<TreePath> treePaths = TreeUtil.collectSelectedPaths(this.simpleTree);
        for (TreePath treePath : treePaths) {
            Object pathComponent = treePath.getPathComponent(treePath.getPathCount() - 1);
            if (pathComponent instanceof MainTopTreeView.RequestMappingNode) {
                RequestMappingModel requestMappingModel = ((MainTopTreeView.RequestMappingNode) pathComponent).getData();
                requestMappingModels.add(requestMappingModel);
            }
            if (pathComponent instanceof MainTopTreeView.ClassNameNode) {
                MainTopTreeView.ClassNameNode classNameNode = (MainTopTreeView.ClassNameNode) pathComponent;
            }
            if (pathComponent instanceof MainTopTreeView.ModuleNode) {
                MainTopTreeView.ModuleNode controllerModuleNode = (MainTopTreeView.ModuleNode) pathComponent;
            }
        }
        apifoxExp.export(toOpenApiJson(requestMappingModels));
    }
}
