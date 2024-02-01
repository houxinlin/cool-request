package com.cool.request.view;

import java.util.HashMap;
import java.util.Map;

public class ToolComponentPageManager {
    public Map<String, ToolComponentPage> toolComponentPageMap = new HashMap<>();

    public void register(Object toolComponentPage) {
        if (toolComponentPage != null && toolComponentPage instanceof ToolComponentPage) {
            toolComponentPageMap.put(((ToolComponentPage) toolComponentPage).getPageId(), ((ToolComponentPage) toolComponentPage)
            );
        }
    }
}
