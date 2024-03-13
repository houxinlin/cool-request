package com.cool.request.utils;

import java.io.IOException;
import java.net.NoRouteToHostException;

public class ExceptionDialogHandlerUtils {
    public static void handlerException(Exception e) {
        if (e instanceof NoRouteToHostException) {
            MessagesWrapperUtils.showErrorDialogWithI18n("host.no.route");
        } else if (e instanceof IOException) {
            MessagesWrapperUtils.showErrorDialogWithI18n("net.err");
        }
    }
}
