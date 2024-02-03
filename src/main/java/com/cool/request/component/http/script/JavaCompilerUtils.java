package com.cool.request.component.http.script;

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.intellij.compiler.JavaInMemoryCompiler;
import com.intellij.compiler.impl.javaCompiler.javac.JavacCompiler;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.project.Project;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class JavaCompilerUtils {

    public static JavaCompiler getSystemJavaCompiler() {


        try {
            JavaCompiler systemJavaCompiler = ToolProvider.getSystemJavaCompiler();
            if (systemJavaCompiler != null) return systemJavaCompiler;
            File file = CoolRequestConfigConstant.CONFIG_JAVAC_PATH.toFile();
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
            Class<?> javaCompilerClass = urlClassLoader.loadClass("javax.tools.JavaCompiler");
            Class<?> javacToolClass = Class.forName("com.sun.tools.javac.api.JavacTool", true, urlClassLoader);
            Class<?> subclass = javacToolClass.asSubclass(javaCompilerClass);
            return (JavaCompiler) subclass.getConstructor().newInstance();
        } catch (Exception ignored) {
        }
        return null;
    }
}
