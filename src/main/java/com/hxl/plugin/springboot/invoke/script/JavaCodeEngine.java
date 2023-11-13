package com.hxl.plugin.springboot.invoke.script;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.utils.ClassResourceUtils;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.hxl.plugin.springboot.invoke.view.page.ScriptPage;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;

public class JavaCodeEngine {
    private static final String REQUEST_CLASS = "com.hxl.plugin.springboot.invoke.script.RequestApi";
    private static final String RESPONSE_CLASS = "com.hxl.plugin.springboot.invoke.script.ResponseApi";
    private static final Logger LOG = Logger.getInstance(ScriptPage.class);

    public boolean execRequest(Request request, String source) {
        if (StringUtils.isEmpty(source)) return true;
        byte[] requestScriptBytes = ClassResourceUtils.read("/plugin-script-request.java");
        if (requestScriptBytes != null) {
            String code = new String(requestScriptBytes).replace("${body}", source);
            try {
                Map<String, Class<?>> result = javac(code, REQUEST_CLASS);
                if (result.get(REQUEST_CLASS) != null) {
                    return invokeRequest(result.get(REQUEST_CLASS), request);
                }
            } catch (Exception e) {
                Messages.showErrorDialog(e.getMessage(),
                        e instanceof CompilationException ?
                                "Request Script Syntax Error ,Please Check!" : "Request Script Run Error");
            }
        }
        return false;
    }

    public boolean execResponse(Response response, String source) {
        if (StringUtils.isEmpty(source)) return true;
        byte[] requestScriptBytes = ClassResourceUtils.read("/plugin-script-response.java");
        if (requestScriptBytes != null) {
            String code = new String(requestScriptBytes).replace("${body}", source);
            try {
                Map<String, Class<?>> result = javac(code, RESPONSE_CLASS);
                if (result.get(RESPONSE_CLASS) != null) {
                    return invokeResponse(result.get(RESPONSE_CLASS), response);
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> Messages.showErrorDialog(e.getMessage(),
                        e instanceof CompilationException ?
                                "Response Script Syntax Error ,Please Check!" : "Request Script Run Error"));
            }
        }
        return false;
    }

    private Map<String, Class<?>> javac(String code, String source) throws Exception {
        ClassResourceUtils.copyTo(getClass().getResource(Constant.CLASSPATH_LIB_PATH), Constant.CONFIG_LIB_PATH.toString());
        InMemoryJavaCompiler inMemoryJavaCompiler = InMemoryJavaCompiler.getInstance().useParentClassLoader(ScriptPage.class.getClassLoader());
        inMemoryJavaCompiler.useOptions("-cp", PathManager.getJarPathForClass(Request.class));
        return inMemoryJavaCompiler.addSource(source, code).compileAll();
    }

    private boolean invokeRequest(Class<?> clas, Request request) throws ScriptExecException {
        try {
            Object instance = clas.getConstructor(Request.class).newInstance(request);
            MethodType methodType = MethodType.methodType(void.class);
            MethodHandle handle = MethodHandles.lookup().findVirtual(clas, "handlerRequest", methodType);
            handle.bindTo(instance).invokeWithArguments();
            return true;
        } catch (Throwable e) {
            LOG.info(e);
            throw new ScriptExecException(e.getMessage());
        }
    }

    private boolean invokeResponse(Class<?> clas, Response response) throws ScriptExecException {
        try {
            Object instance = clas.getConstructor(Response.class).newInstance(response);
            MethodType methodType = MethodType.methodType(void.class);
            MethodHandle handle = MethodHandles.lookup().findVirtual(clas, "handlerResponse", methodType);
            handle.bindTo(instance).invokeWithArguments();
            return true;
        } catch (Throwable e) {
            LOG.info(e);
            throw new ScriptExecException(e.getMessage());
        }
    }

}
