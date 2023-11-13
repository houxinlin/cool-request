package com.hxl.plugin.springboot.invoke.script;

import com.hxl.plugin.springboot.invoke.Constant;
import com.intellij.openapi.ui.Messages;

import javax.tools.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class InMemoryJavaCompiler {
    private final JavaCompiler javac;
    private DynamicClassLoader classLoader;
    private Iterable<String> options;
    boolean ignoreWarnings = false;
    private final Map<String, SourceCode> sourceCodes = new HashMap<String, SourceCode>();
    private static InMemoryJavaCompiler instance = null;
    public synchronized static InMemoryJavaCompiler getInstance() {
        if (instance == null)
            instance = new InMemoryJavaCompiler();
        return instance;
    }

    private JavaCompiler getSystemJavaCompiler() {
        try {
            JavaCompiler systemJavaCompiler = ToolProvider.getSystemJavaCompiler();
            if (systemJavaCompiler != null) return systemJavaCompiler;
            File file = Constant.CONFIG_JAVAC_PATH.toFile();
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
            Class<?> javaCompilerClass = urlClassLoader.loadClass("javax.tools.JavaCompiler");
            Class<?> javacToolClass = Class.forName("com.sun.tools.javac.api.JavacTool", true, urlClassLoader);
            Class<?> subclass = javacToolClass.asSubclass(javaCompilerClass);
            return (JavaCompiler) subclass.getConstructor().newInstance();
        } catch (Exception ignored) {
        }
        return null;
    }

    private InMemoryJavaCompiler() {
        this.javac = getSystemJavaCompiler();
        this.classLoader = new DynamicClassLoader(ClassLoader.getSystemClassLoader());
    }

    public InMemoryJavaCompiler useParentClassLoader(ClassLoader parent) {
        this.classLoader = new DynamicClassLoader(parent);
        return this;
    }

    public ClassLoader getClassloader() {
        return classLoader;
    }

    public InMemoryJavaCompiler useOptions(String... options) {
        this.options = Arrays.asList(options);
        return this;
    }

    public InMemoryJavaCompiler ignoreWarnings() {
        ignoreWarnings = true;
        return this;
    }

    public Map<String, Class<?>> compileAll() throws Exception {
        if (sourceCodes.isEmpty()) {
            return new HashMap<>();
        }
        Collection<SourceCode> compilationUnits = sourceCodes.values();
        CompiledCode[] code;

        code = new CompiledCode[compilationUnits.size()];
        Iterator<SourceCode> iter = compilationUnits.iterator();
        for (int i = 0; i < code.length; i++) {
            code[i] = new CompiledCode(iter.next().getClassName());
        }
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        ExtendedStandardJavaFileManager fileManager = new ExtendedStandardJavaFileManager(javac.getStandardFileManager(null, null, null), classLoader);
        JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, collector, options, null, compilationUnits);
        boolean result = task.call();
        if (!result || !collector.getDiagnostics().isEmpty()) {
            StringBuffer exceptionMsg = new StringBuffer();
            boolean hasWarnings = false;
            boolean hasErrors = false;
            for (Diagnostic<? extends JavaFileObject> d : collector.getDiagnostics()) {
                switch (d.getKind()) {
                    case NOTE:
                    case MANDATORY_WARNING:
                    case WARNING:
                        hasWarnings = true;
                        break;
                    case OTHER:
                    case ERROR:
                    default:
                        hasErrors = true;
                        break;
                }
                exceptionMsg.append("line=:")
                        .append(d.getLineNumber() - 19)
                        .append(" , ")
                        .append(d.getMessage(Locale.US));
                exceptionMsg.append("\n");
            }
            if (hasWarnings && !ignoreWarnings || hasErrors) {
                throw new CompilationException(exceptionMsg.toString());
            }
        }
        Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
        for (String className : sourceCodes.keySet()) {
            classes.put(className, classLoader.loadClass(className));
        }
        return classes;
    }

    public Class<?> compile(String className, String sourceCode) throws Exception {
        return addSource(className, sourceCode).compileAll().get(className);
    }

    public InMemoryJavaCompiler addSource(String className, String sourceCode) throws Exception {
        sourceCodes.put(className, new SourceCode(className, sourceCode));
        return this;
    }
}
