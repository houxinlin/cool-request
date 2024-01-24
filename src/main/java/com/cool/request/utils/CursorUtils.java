package com.cool.request.utils;

import java.awt.*;

public class CursorUtils {
    public static void setWait(Component component){
        Cursor waitingCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        component.setCursor(waitingCursor);
    }
    public static void setDefault(Component component){
        Cursor waitingCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        component.setCursor(waitingCursor);
    }
}
