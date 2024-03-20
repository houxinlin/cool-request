package com.cool.request.components.scheduled;

import com.cool.request.common.bean.components.BasicComponent;
import com.cool.request.components.ComponentType;
import com.cool.request.components.JavaClassComponent;
import com.cool.request.utils.ComponentIdUtils;
import com.cool.request.utils.NavigationUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BasicScheduled extends BasicComponent implements JavaClassComponent, Serializable {
    private static final long serialVersionUID = 1000000000;
    private String moduleName;
    private int serverPort;
    private String className;
    private String methodName;
    private transient List<PsiMethod> ownerPsiMethod = new ArrayList<>();

    public List<PsiMethod> getOwnerPsiMethod() {
        return ownerPsiMethod;
    }

    public void setOwnerPsiMethod(List<PsiMethod> ownerPsiMethod) {
        this.ownerPsiMethod = ownerPsiMethod;
    }

    @Override
    public void calcId(Project project) {
        setId(ComponentIdUtils.getMd5(project, this));
    }

    @Override
    public void goToCode(Project project) {
        NavigationUtils.jumpToSpringScheduledMethod(project, this);
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.SCHEDULE;
    }

    @Override
    public String getJavaClassName() {
        return className;
    }

    @Override
    public String getUserProjectModuleName() {
        return moduleName;
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }

    @Override
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
