/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ProjectUtils.java is part of Cool Request
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

package com.cool.request.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.io.URLUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.cool.request.common.constant.CoolRequestConfigConstant.SCRIPT_NAME;

public class ProjectUtils {
    public static Project getCurrentProject() {
        return ProjectManager.getInstance().getOpenProjects()[0];
    }

    /**
     * 获取用户项目通过maven、gradle引入的第三方jar包
     *
     * @param project
     * @return
     */
    public static List<String> getUserProjectIncludeLibrary(Project project) {
        List<String> libraryNames = new ArrayList<>();
        for (Module module : ModuleManager.getInstance(project).getModules()) {
            ModuleRootManager.getInstance(module).orderEntries().forEachLibrary(library -> {
                String[] urls = library.getUrls(OrderRootType.CLASSES);
                if (urls.length > 0) {
                    libraryNames.add(library.getUrls(OrderRootType.CLASSES)[0]);
                }
                return true;
            });
        }
        return libraryNames.stream().map(jarUrl -> {
            String jarFile = jarUrl;
            //上面获取的是jar://x.jar!/格式的数据，这里需要去除掉
            if (jarFile.startsWith("jar://") && jarFile.endsWith("!/")) {
                jarFile = jarFile.substring(6);
                jarFile = jarFile.substring(0, jarFile.length() - 2);
            }
            return jarFile;
        }).collect(Collectors.toList());
    }

    public static List<String> getClassOutputPaths(Project project) {
        List<String> result = new ArrayList<>();
        for (Module module : ModuleManager.getInstance(project).getModules()) {
            try {
                String path = CompilerModuleExtension.getInstance(module).getCompilerOutputPath().getPath();
                result.add(path);
                //这里有的模块获取不到，直接忽略
            } catch (Exception e) {
            }
        }
        return result;
    }

    private static Module findMainModule(Project project) {
        Module result = null;
        int moduleNameLength = Integer.MAX_VALUE;
        for (Module module : ModuleManager.getInstance(project).getModules()) {
            if (module.getName().length() < moduleNameLength) {
                moduleNameLength = module.getName().length();
                result = module;
            }
        }
        return result;
    }

    private static VirtualFile createScriptLibVirtualFile(String jarPath) {
        String pathUrl = VirtualFileManager.constructUrl(URLUtil.JAR_PROTOCOL, jarPath + JarFileSystem.JAR_SEPARATOR);
        return VirtualFileManager.getInstance().findFileByUrl(pathUrl);
    }

    /**
     * 修正脚本lib路径，可能被用户篡改
     */
    private static void fixScriptLibPath(Project project, String path) {
        LibraryTable projectLibraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        final LibraryTable.ModifiableModel projectLibraryModel = projectLibraryTable.getModifiableModel();
        Library scriptLib = projectLibraryTable.getLibraryByName(SCRIPT_NAME);
        if (scriptLib != null) {
            Library.ModifiableModel modifiableModel = scriptLib.getModifiableModel();
            for (String url : modifiableModel.getUrls(OrderRootType.CLASSES)) {
                modifiableModel.removeRoot(url, OrderRootType.CLASSES);
            }
            modifiableModel.addRoot(createScriptLibVirtualFile(path), OrderRootType.CLASSES);
            SwingUtilities.invokeLater(() -> ApplicationManager.getApplication().runWriteAction(() -> {
                modifiableModel.commit();
                projectLibraryModel.commit();
            }));

        }
    }

    private static boolean isExistInModule(Module module) {
        OrderEntry[] orderEntries = ModuleRootManager.getInstance(module).getOrderEntries();
        for (OrderEntry orderEntry : orderEntries) {
            if (orderEntry instanceof LibraryOrderEntry) {
                String libraryName = ((LibraryOrderEntry) orderEntry).getLibraryName();
                if (SCRIPT_NAME.equals(libraryName)) return true;
            }
        }
        return false;
    }


    private static void addScriptLibToProject(Project project, String jarPath) {
        LibraryTable projectLibraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        final LibraryTable.ModifiableModel projectLibraryModel = projectLibraryTable.getModifiableModel();

        Library scriptLib = projectLibraryTable.getLibraryByName(SCRIPT_NAME);
        VirtualFile scriptLibVirtualFile = createScriptLibVirtualFile(jarPath);
        if (scriptLib == null) {
            scriptLib = projectLibraryModel.createLibrary(SCRIPT_NAME);
            Library.ModifiableModel libraryModel = scriptLib.getModifiableModel();
            libraryModel.addRoot(scriptLibVirtualFile, OrderRootType.CLASSES);
            ApplicationManager.getApplication().runWriteAction(() -> {
                libraryModel.commit();
                projectLibraryModel.commit();
            });
        }
    }

    private static void addScriptLibToMainModule(Project project) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        Module mainModule = findMainModule(project);
        if (mainModule == null && modules.length > 0) mainModule = modules[modules.length - 1];
        if (mainModule == null) return;
        if (isExistInModule(mainModule)) return;
        LibraryTable projectLibraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        Library scriptLib = projectLibraryTable.getLibraryByName(SCRIPT_NAME);

        Module finalMainModule = mainModule;
        ApplicationManager.getApplication().invokeLater(() -> {
            ModuleRootModificationUtil.addDependency(finalMainModule, scriptLib, DependencyScope.COMPILE, false);
        });

    }

    public static boolean isInstall(Project project) {
        LibraryTable projectLibraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        Library scriptLib = projectLibraryTable.getLibraryByName(SCRIPT_NAME);
        return scriptLib != null;
    }

    public static void addDependency(Project project, String jarPath) {
        fixScriptLibPath(project, jarPath);
        addScriptLibToProject(project, jarPath);
        addScriptLibToMainModule(project);
    }
}
