package com.cool.request.scan;

import com.cool.request.components.http.Controller;
import com.cool.request.components.http.StaticController;
import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.components.scheduled.BasicScheduled;
import com.cool.request.scan.jaxrs.JaxRsControllerConverter;
import com.cool.request.scan.jaxrs.JaxRsControllerScan;
import com.cool.request.scan.rose.RoseControllerConverter;
import com.cool.request.scan.rose.RoseControllerScan;
import com.cool.request.scan.spring.SpringControllerScan;
import com.cool.request.scan.spring.SpringMvcControllerConverter;
import com.cool.request.scan.spring.SpringScheduledScan;
import com.cool.request.utils.PsiUtils;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public final class Scans implements ControllerScan, ScheduledScan, ControllerConverter {
    public static Scans getInstance(Project project) {
        return project.getService(Scans.class);
    }

    private static final List<AnnotationControllerScan> annotationControllerScans =
            Arrays.asList(new JaxRsControllerScan(),
                    new SpringControllerScan(),
                    new RoseControllerScan());

    private static final List<ControllerConverter> controllerConverter = Arrays.asList(
            new JaxRsControllerConverter(),
            new SpringMvcControllerConverter(),
            new RoseControllerConverter());

    private SpringScheduledScan springScheduledScan = new SpringScheduledScan();

    @Override
    public List<Controller> scanController(Project project) {
        List<Controller> result = new ArrayList<>();
        for (AnnotationControllerScan annotationControllerScan : annotationControllerScans) {
            result.addAll(annotationControllerScan.scanController(project));
        }
        return result;
    }

    @Override
    public List<BasicScheduled> scanScheduled(Project project) {
        return springScheduledScan.scanScheduled(project);
    }

    public void goToCode(Project project, Controller controller) {
        for (ControllerConverter converter : controllerConverter) {
            PsiMethod psiMethod = converter.controllerToPsiMethod(project, controller);
            if (psiMethod != null) {
                PsiUtils.methodNavigate(psiMethod);
                return;
            }
        }
    }

    @Override
    public List<HttpMethod> parseHttpMethod(PsiMethod psiMethod) {
        return null;
    }

    @Override
    public PsiMethod controllerToPsiMethod(Project project, Controller controller) {
        for (ControllerConverter converter : controllerConverter) {
            PsiMethod psiMethod = converter.controllerToPsiMethod(project, controller);
            if (psiMethod != null) return psiMethod;
        }
        return null;
    }

    @Override
    public List<StaticController> psiMethodToController(Project project, PsiClass originClass, Module module, PsiMethod psiMethod) {
        for (ControllerConverter converter : controllerConverter) {
            if (converter.canConverter(psiMethod)) {
                return converter.psiMethodToController(project, originClass, module, psiMethod);
            }
        }
        return null;
    }
}
