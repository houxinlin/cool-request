# springboot-invoke-plugin

这个插件足够让你兴奋

它用于解决Controller方法和定时器方法的调用。非常方便。

# 使用步骤：
1. 安装插件
2. 在"View"菜单中选择"SpringBeanInvoke"视图工具窗口
3. 重启你的项目
项目启动后，所有的定时器和Controller方法都将显示出来。双击进行调用。

## 问题

1. 什么是代理对象和原始对象？

   答：该插件不通过HTTP请求调用Controller，而是通过反射在内部调用。因此，在获取对象时，对象可能被CGLIB代理，但你可以选择原始对象，但某些AOP可能无法调用。


2. 什么是拦截器？

   如果你的项目有一个拦截器与该Controller匹配，在选择应用拦截器时，该拦截器将首先被调用。如果未选择拦截器，即使拦截器匹配该Controller，也不会被调用。这是该插件的功能之一，用于在没有身份验证时调试Controller。

# 截图

![image](https://github.com/houxinlin/springboot-invoke-plugin/assets/38684327/de98d3ef-c661-4a03-8d81-1c153cfc92b8)

![image](https://github.com/houxinlin/springboot-invoke-plugin/assets/38684327/49a22b51-9a7d-4725-af10-f9187ac3dc2b)


## Build Source Code

```cmd
./gradlew jar
```
### Install
    1. Open you Idea
    2. Open Plugin Setting
    3. Install Plugin For Disk
