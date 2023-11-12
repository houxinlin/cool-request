package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.script.InMemoryJavaCompiler;
import com.hxl.plugin.springboot.invoke.script.Request;
import com.hxl.plugin.springboot.invoke.utils.ClassResourceUtils;
import com.hxl.plugin.springboot.invoke.utils.NotifyUtils;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;


public class ScriptPage extends BasicEditPage {
    private static final String REQUEST_CLASS = "com.hxl.plugin.springboot.invoke.script.RequestApi";

    public void execRequest(Request request) {
        ClassResourceUtils.copyTo(getClass().getResource(Constant.CLASSPATH_LIB_PATH), Constant.CONFIG_LIB_PATH.toString());
        InMemoryJavaCompiler inMemoryJavaCompiler = InMemoryJavaCompiler.newInstance().useParentClassLoader(ScriptPage.class.getClassLoader());
        inMemoryJavaCompiler.useOptions("-cp", PathManager.getJarPathForClass(Request.class));
        byte[] requestScriptBytes = ClassResourceUtils.read("/plugin-script-request.java");
        if (requestScriptBytes != null) {
            String code = new String(requestScriptBytes).replace("${body}", getText());
            Map<String, Class<?>> result = null;
            try {
                result = inMemoryJavaCompiler.addSource(REQUEST_CLASS, code).compileAll();
                if (result.get(REQUEST_CLASS) != null) {
                    invokeRequest(result.get(REQUEST_CLASS), request);
                }
            } catch (Exception e) {
                NotifyUtils.notification("Request Code Err");
            }
        }
    }

    private void invokeRequest(Class<?> clas, Request request) {
        try {
            Object instance = clas.newInstance();
            MethodType methodType = MethodType.methodType(void.class, Request.class);
            MethodHandle handle = MethodHandles.lookup().findVirtual(clas, "handlerRequest", methodType);
            handle.bindTo(instance).invokeWithArguments(request);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public void execResponse() {

    }

    public ScriptPage(Project project) {
        super(project);
    }

    @Override
    public FileType getFileType() {
        return PlainTextFileType.INSTANCE;
    }
}
