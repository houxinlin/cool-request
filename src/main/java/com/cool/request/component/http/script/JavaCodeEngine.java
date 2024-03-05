package com.cool.request.component.http.script;


import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.common.state.SettingsState;
import com.cool.request.script.HTTPRequest;
import com.cool.request.script.HTTPResponse;
import com.cool.request.script.ILog;
import com.cool.request.utils.ClassResourceUtils;
import com.cool.request.utils.ProjectUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.page.ScriptPage;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JavaCodeEngine {
    private static final String REQUEST_REGEX = "(class\\s+CoolRequestScript\\s*\\{)";
    private static final String RESPONSE_REGEX = "(class\\s+CoolResponseScript\\s*\\{)";
    public static final String REQUEST_CLASS = "com.cool.request.script.CoolRequestScript";
    public static final String RESPONSE_CLASS = "com.cool.request.script.CoolResponseScript";
    private static final Logger LOG = Logger.getInstance(ScriptPage.class);
    private final InMemoryJavaCompiler inMemoryJavaCompiler = new InMemoryJavaCompiler();
    private final Project project;

    public JavaCodeEngine(Project project) {
        this.project = project;
    }

    /**
     * 执行前置脚本
     *
     * @return 如果返回false，则表示被脚本拒绝执行
     */
    public boolean execRequest(Request request, String code) throws Exception {
        if (StringUtils.isEmpty(code)) {
            return true;
        }
        InMemoryJavaCompiler memoryJavaCompiler = javac(prependPublicToCoolRequestScript(REQUEST_REGEX, code), REQUEST_CLASS);
        Class<?> requestClass = memoryJavaCompiler.getClassloader().loadClass(REQUEST_CLASS);
        return invokeRequest(requestClass, request);
    }

    /**
     * 执行后置脚本
     */
    public boolean execResponse(Response response, String code, ILog iLog) {
        if (StringUtils.isEmpty(code)) {
            return true;
        }
        try {
            InMemoryJavaCompiler memoryJavaCompiler = javac(prependPublicToCoolRequestScript(RESPONSE_REGEX, code), RESPONSE_CLASS);
            Class<?> responseClass = memoryJavaCompiler.getClassloader().loadClass(RESPONSE_CLASS);
            return invokeResponse(responseClass, response, iLog);
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> Messages.showErrorDialog(e.getMessage(),
                    e instanceof CompilationException ?
                            "Response Script Syntax Error ,Please Check!" : "Response Script Run Error"));
        }
        return false;
    }

    /**
     * 创建Classloader
     */
    private ClassLoader createClassLoader() {
        List<URL> userLibrary = ProjectUtils.getUserProjectIncludeLibrary(project)
                .stream()
                .map(StringUtils::fileToURL)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        URLClassLoader urlClassLoader = new URLClassLoader(userLibrary.toArray(URL[]::new), JavaCodeEngine.class.getClassLoader());

        List<URL> classOutputPaths = ProjectUtils.getClassOutputPaths(project)
                .stream()
                .map(StringUtils::fileToURL)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new URLClassLoader(classOutputPaths.toArray(URL[]::new), urlClassLoader);

    }


    /**
     * 编译脚本
     */
    public InMemoryJavaCompiler javac(String code, String source) throws Exception {
        ClassResourceUtils.copyTo(getClass().getResource(CoolRequestConfigConstant.CLASSPATH_LIB_PATH), CoolRequestConfigConstant.CONFIG_LIB_PATH.toString());

        InMemoryJavaCompiler javaCompiler = inMemoryJavaCompiler.useParentClassLoader(createClassLoader(), new ArrayList<>());
        javaCompiler.useOptions("-proc:none", "-encoding", "utf-8", "-cp", getJavacCommandLibrary());
        javaCompiler.addSource(source, code).compileAll();
        return javaCompiler;
    }

    /**
     * 获取脚本编译时候的-cp命令参数
     */
    private String getJavacCommandLibrary() {
        List<String> projectLibrary = new ArrayList<>();
        projectLibrary.add(PathManager.getJarPathForClass(HTTPRequest.class)); //必须依赖的
        SettingsState settingsState = SettingPersistentState.getInstance().getState();
        if (settingsState.enabledScriptLibrary) {
            projectLibrary.addAll(ProjectUtils.getUserProjectIncludeLibrary(project));
            projectLibrary.addAll(ProjectUtils.getClassOutputPaths(project)); //用户项目自己的输出路劲
        }
        return String.join(File.pathSeparator, projectLibrary);
    }

    /**
     * 调用请求脚本中的方法
     */
    private boolean invokeRequest(Class<?> requestClass, Request request) throws ScriptExecException {
        try {
            Object instance = requestClass.getConstructor().newInstance();
            MethodType methodType = MethodType.methodType(boolean.class, ILog.class, HTTPRequest.class);
            MethodHandle handle = MethodHandles.lookup().findVirtual(requestClass, "handlerRequest", methodType);
            Object result = handle.bindTo(instance).invokeWithArguments(request.getScriptSimpleLog(), request);
            if (result instanceof Boolean) {
                return ((boolean) result);
            }
            return true;
        } catch (Throwable e) {
            LOG.info(e);
            throw new ScriptExecException(e.getMessage());
        }
    }

    /**
     * 调用响应脚本中的方法
     */

    private boolean invokeResponse(Class<?> responseClass, Response response, ILog iLog) throws ScriptExecException {
        try {
            Object instance = responseClass.getConstructor().newInstance();
            MethodType methodType = MethodType.methodType(void.class, ILog.class, HTTPResponse.class);
            MethodHandle handle = MethodHandles.lookup().findVirtual(responseClass, "handlerResponse", methodType);
            handle.bindTo(instance).invokeWithArguments(iLog, response);
            return true;
        } catch (Throwable e) {
            LOG.info(e);
            throw new ScriptExecException(e.getMessage());
        }
    }

    /**
     * 编辑器检测不到有真实的文件后会报错，只能在编辑时候把public去掉，在编译时候加上
     */
    public static String prependPublicToCoolRequestScript(String regex, String input) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String replacement = "public " + matcher.group(1);
            return matcher.replaceFirst(replacement);
        } else {
            return input; // 如果未找到匹配项，返回原始字符串
        }
    }
}
