package com.cool.request.component;

import com.cool.request.common.bean.components.Component;

/**
 * Java类中的某个组件，未来可能组件不是Java类中的数据，并且Java类可以导航到具体代码
 */
public interface JavaClassComponent extends CodeNavigation, Component {
    public String getJavaClassName();

    public String getUserProjectModuleName();

    public void setModuleName(String moduleName);

    public String getModuleName();

}
