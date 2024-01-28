package com.cool.request.action.actions;


import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.utils.ProjectUtils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.events.IToolBarViewEvents;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.CompilerModuleExtension;
import org.jetbrains.annotations.NotNull;

public class HelpAction extends BaseAnAction {
    private final IToolBarViewEvents iViewEvents;

    public HelpAction(Project project, IToolBarViewEvents iViewEvents) {
        super(project, () -> ResourceBundleUtils.getString("help"),
                () -> ResourceBundleUtils.getString("help"), CoolRequestIcons.HELP);
        this.iViewEvents = iViewEvents;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        for (Module module : ModuleManager.getInstance(e.getProject()).getModules()) {
           try {
               String path = CompilerModuleExtension.getInstance(module).getCompilerOutputPath().getPath();
               System.out.println(path);
           }catch (Exception exception){}
        }
        ProjectUtils.addDependency(e.getProject(),"D:\\project\\java\\springboot-invoke-plugin\\deps\\cool-request-script-api-1.0-SNAPSHOT.jar");
//        iViewEvents.pluginHelp();
    }
}
