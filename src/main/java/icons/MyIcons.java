package icons;


import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public interface MyIcons {

    Icon MAIN = IconLoader.getIcon("/icons/pluginIcon.svg", MyIcons.class);
    Icon GET_METHOD=IconLoader.getIcon("/icons/GET.png", MyIcons.class);
    Icon POST_METHOD=IconLoader.getIcon("/icons/POST.png", MyIcons.class);
    Icon PUT_METHOD=IconLoader.getIcon("/icons/PUT.png", MyIcons.class);
    Icon DELTE_METHOD=IconLoader.getIcon("/icons/DELETE.png", MyIcons.class);
    Icon CLOSE=IconLoader.getIcon("/icons/CLOSE.png", MyIcons.class);
    Icon APIFOX=IconLoader.getIcon("/icons/apifox.png", MyIcons.class);
    Icon OPENAPI=IconLoader.getIcon("/icons/OPENAPI.png", MyIcons.class);

    Icon DELETE=IconLoader.getIcon("/icons/IC_DELETE.png", MyIcons.class);

    Icon IC_HELP=IconLoader.getIcon("/icons/IC_HELP.png", MyIcons.class);
    Icon IC_METHOD=IconLoader.getIcon("/icons/METHOD.png", MyIcons.class);
    Icon IC_HTTP=IconLoader.getIcon("/icons/svg/http_request.svg", MyIcons.class);
    Icon CURL=IconLoader.getIcon("/icons/svg/curl.svg", MyIcons.class);

}