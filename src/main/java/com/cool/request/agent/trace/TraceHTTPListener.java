package com.cool.request.agent.trace;

import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.components.http.CustomController;
import com.cool.request.components.http.net.RequestContext;
import com.cool.request.rmi.agent.AgentRMIManager;
import com.cool.request.rmi.agent.ICoolRequestAgentRMIInterface;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.main.HTTPEventListener;
import com.cool.request.view.page.TracePreviewView;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;

import java.rmi.RemoteException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class TraceHTTPListener implements HTTPEventListener {
    private TracePreviewView tracePreviewView;
    private Project project;
    private Map<Supplier<Boolean>, Function<ICoolRequestAgentRMIInterface, List<String>>> framework = new HashMap<>();

    public TraceHTTPListener(Project project, TracePreviewView tracePreviewView) {
        this.tracePreviewView = tracePreviewView;
        this.project = project;

        framework.put(() -> SettingPersistentState.getInstance().getState().traceMybatis, coolRequestAgentRMIInterface ->
                new MybatisFramework().addTraceObject(coolRequestAgentRMIInterface));
    }

    private void addMethodHook(ICoolRequestAgentRMIInterface coolRequestAgentRMIInterface, String className, String methodName, List<String> cache) {
        try {
            coolRequestAgentRMIInterface.addMethodHook(className, methodName, 1, false, useCache(), useLog());
            cache.add(className + "." + methodName);
        } catch (RemoteException ignored) {

        }
    }

    private boolean useCache() {
        return SettingPersistentState.getInstance().getState().userByteCodeCache;

    }

    private boolean useLog() {
        return SettingPersistentState.getInstance().getState().useTraceLog;
    }

    @Override
    public void beginSend(RequestContext requestContext, ProgressIndicator progressIndicator) {
        tracePreviewView.setTraceFrame(new ArrayList<>());
        if (requestContext.getController() instanceof CustomController) return;
        if (!SettingPersistentState.getInstance().getState().enabledTrace) return;
        Map<String, Set<String>> customMethodMap = AgentRMIManager.getAgentRMIManager(project).getCustomMethodMap();
        boolean userByteCodeCache = SettingPersistentState.getInstance().getState().userByteCodeCache;
        boolean useTraceLog = SettingPersistentState.getInstance().getState().useTraceLog;
        progressIndicator.setText("Trace init...");
        Set<String> alreadyAdd = new HashSet<>();
        for (ICoolRequestAgentRMIInterface coolRequestAgentRMIInterface :
                AgentRMIManager.getAgentRMIManager(project).getCoolRequestAgentRMIInterfaces()) {
            try {

                if (!coolRequestAgentRMIInterface.ping()) return;
                //增加第三方框架
                for (Supplier<Boolean> frameworkSupplier : framework.keySet()) {
                    if (frameworkSupplier.get()) {
                        List<String> result = framework.get(frameworkSupplier).apply(coolRequestAgentRMIInterface);
                        if (result != null) {
                            alreadyAdd.addAll(result);
                        }
                    }
                }
                if (!StringUtils.isEmpty(requestContext.getController().getSimpleClassName()) &&
                        !StringUtils.isEmpty(requestContext.getController().getSimpleClassName())) {
                    progressIndicator.setText("Trace " + requestContext.getController().getSimpleClassName()
                            + "." +
                            requestContext.getController().getMethodName());
                    coolRequestAgentRMIInterface.addMethodHook(
                            requestContext.getController().getSimpleClassName(),
                            requestContext.getController().getMethodName(),
                            SettingPersistentState.getInstance().getState().maxTraceDepth,
                            true,
                            userByteCodeCache,
                            useTraceLog);

                }
                alreadyAdd.add(requestContext.getController().getSimpleClassName() + "." + requestContext.getController().getMethodName());
                for (String className : customMethodMap.keySet()) {
                    for (String methodName : customMethodMap.getOrDefault(className, new HashSet<>())) {
                        if (alreadyAdd.contains(className + "." + methodName)) continue;
                        progressIndicator.setText("Trace " + className
                                + "." +
                                methodName);
                        coolRequestAgentRMIInterface.addMethodHook(className,
                                methodName,
                                1,
                                false,
                                userByteCodeCache,
                                useTraceLog);
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    interface Framework {
        public List<String> addTraceObject(ICoolRequestAgentRMIInterface coolRequestAgentRMIInterface);
    }

    class MybatisFramework implements Framework {
        @Override
        public List<String> addTraceObject(ICoolRequestAgentRMIInterface coolRequestAgentRMIInterface) {
            MethodRegister methodRegister = new MethodRegister();
            methodRegister.register("org.apache.ibatis.executor.SimpleExecutor", "doQuery");
            methodRegister.register("org.apache.ibatis.executor.SimpleExecutor", "doQueryCursor");
            methodRegister.register("org.apache.ibatis.executor.SimpleExecutor", "doUpdate");
            methodRegister.register("org.apache.ibatis.executor.ReuseExecutor", "doQuery");
            methodRegister.register("org.apache.ibatis.executor.ReuseExecutor", "doQueryCursor");
            methodRegister.register("org.apache.ibatis.executor.ReuseExecutor", "doUpdate");
            methodRegister.register("org.apache.ibatis.executor.BatchExecutor", "doQuery");
            methodRegister.register("org.apache.ibatis.executor.BatchExecutor", "doQueryCursor");
            methodRegister.register("org.apache.ibatis.executor.BatchExecutor", "doUpdate");
            return methodRegister.addMethod(coolRequestAgentRMIInterface);
        }
    }

    class MethodRegister {
        private Map<String, List<String>> methodRegister = new HashMap<>();

        public void register(String className, String methodName) {
            methodRegister.computeIfAbsent(className, s -> new ArrayList<>()).add(methodName);
        }

        public List<String> addMethod(ICoolRequestAgentRMIInterface coolRequestAgentRMIInterface) {
            List<String> hooked = new ArrayList<>();
            methodRegister.forEach((className, method) -> {
                for (String met : method) {
                    addMethodHook(coolRequestAgentRMIInterface, className, met, hooked);
                }
            });
            return hooked;
        }
    }

}
