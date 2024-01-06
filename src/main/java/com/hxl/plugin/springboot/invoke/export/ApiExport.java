package com.hxl.plugin.springboot.invoke.export;

import java.util.Map;

public interface ApiExport {

    /**
     * 导出
     * @param json
     * @return
     */
    boolean export(String json);

    /**
     * 判断是否可以导出
     * @return
     */
    boolean canExport();

    /**
     * 不能导出时展示条件
     */
    void showCondition();

    /**
     * 检测Token
     * @param exportCondition
     * @return
     */
    Map<String,Boolean> checkToken(ExportCondition  exportCondition);
}
