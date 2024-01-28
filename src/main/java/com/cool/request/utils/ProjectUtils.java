package com.cool.request.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.DependencyScope;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.io.URLUtil;

import static com.cool.request.common.constant.CoolRequestConfigConstant.SCRIPT_NAME;

public class ProjectUtils {
    public static Project getCurrentProject(){
        return ProjectManager.getInstance().getOpenProjects()[0];
    }

    public static void addDependency(Project project, String jarPath) {
        LibraryTable projectLibraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        final LibraryTable.ModifiableModel projectLibraryModel = projectLibraryTable.getModifiableModel();
        Module[] modules = ModuleManager.getInstance(project).getModules();
        Library library = projectLibraryModel.getLibraryByName(SCRIPT_NAME);
        if (library != null) return;
        library = projectLibraryModel.createLibrary(SCRIPT_NAME);
        final Library.ModifiableModel libraryModel = library.getModifiableModel();

        String pathUrl = VirtualFileManager.constructUrl(URLUtil.JAR_PROTOCOL, jarPath + JarFileSystem.JAR_SEPARATOR);
        VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(pathUrl);
        if (file != null) {
            libraryModel.addRoot(file, OrderRootType.CLASSES);
            Library finalLibrary = library;
            ApplicationManager.getApplication().runWriteAction(() -> {
                libraryModel.commit();
                projectLibraryModel.commit();
                ModuleRootModificationUtil.addDependency(modules[0], finalLibrary, DependencyScope.RUNTIME, true);
            });
        }
    }
}
