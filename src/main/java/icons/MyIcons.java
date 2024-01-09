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
    Icon IMAGE=IconLoader.getIcon("/icons/svg/image.svg", MyIcons.class);
    Icon TEXT=IconLoader.getIcon("/icons/svg/text.svg", MyIcons.class);
    Icon HTML=IconLoader.getIcon("/icons/svg/html.svg", MyIcons.class);
    Icon XML=IconLoader.getIcon("/icons/svg/xml.svg", MyIcons.class);
    Icon UPDATE=IconLoader.getIcon("/icons/svg/pluginUpdate.svg", MyIcons.class);
    Icon DEBUG=IconLoader.getIcon("/icons/svg/debug.svg", MyIcons.class);
    Icon CHAT=IconLoader.getIcon("/icons/svg/chat.svg", MyIcons.class);
    Icon LIGHTNING=IconLoader.getIcon("/icons/svg/lightning.svg", MyIcons.class);
}