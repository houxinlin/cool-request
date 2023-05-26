package icons;


import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public interface MyIcons {

    Icon MAIN = IconLoader.getIcon("/icons/pluginIcon.svg", MyIcons.class);
    Icon GET_METHOD=IconLoader.getIcon("/icons/GET.png", MyIcons.class);
    Icon POST_METHOD=IconLoader.getIcon("/icons/POST.png", MyIcons.class);
    Icon PUT_METHOD=IconLoader.getIcon("/icons/PUT.png", MyIcons.class);
    Icon DELTE_METHOD=IconLoader.getIcon("/icons/DELETE.png", MyIcons.class);


}