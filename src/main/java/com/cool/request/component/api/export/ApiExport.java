package com.cool.request.component.api.export;

import java.util.Map;

public interface ApiExport {

    /**
     * 导出
     * @param json 导出的json
     * @return 是否导出成功
     */
    boolean export(String json);

    /**
     * 判断是否可以导出
     * @return 是否可以导出
     */
    boolean canExport();

    /**
     * 不能导出时展示条件
     */
    void showCondition();

    /**
     * 检测Token
     * @param exportCondition 导出条件
     * @return 检测结果
     */
    Map<String,Boolean> checkToken(ExportCondition  exportCondition);
}
