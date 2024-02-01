package com.cool.request.view;

/**
 * 所有工具组件需要实现这个接口,待定后续功能
 */
public interface ToolComponentPage {
    /**
     * 视图得id
     *
     * @return
     */
    public String getPageId();


    /**
     * 从其他页面跳转过来得时候会调用这个方法，附加数据
     *
     * @param object
     */
    public void setAttachData(Object object);
}
