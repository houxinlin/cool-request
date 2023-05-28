# springboot-invoke-plugin

它用于解决Controller方法和定时器方法的调用。非常方便。

# 使用步骤：
1. 安装插件
2. 在"View"菜单中选择"SpringBeanInvoke"视图工具窗口
3. 重启你的项目
项目启动后，所有的定时器和Controller方法都将显示出来。

## 问题

1. 什么是代理对象和原始对象？

   答：该插件不通过HTTP请求调用Controller，而是通过反射在内部调用。因此，在获取对象时，对象可能被CGLIB代理，但你可以选择原始对象，但某些AOP可能在这个过程中失效。


2. 什么是拦截器？

   如果你的项目有一个拦截器与该Controller匹配，在选择应用拦截器时，该拦截器将首先被调用。如果未选择拦截器，即使拦截器匹配该Controller，也不会被调用。这是该插件诞生的初衷之一，用于在没有身份验证时调试Controller。

# 截图

![Peek 2023-05-28 16-50](https://github.com/houxinlin/springboot-invoke-plugin/assets/38684327/e387c47b-0cc6-4c9e-9d8e-9a244cdf7bea)


![Peek 2023-05-28 16-51](https://github.com/houxinlin/springboot-invoke-plugin/assets/38684327/45383654-15b1-48d4-ac08-eb87981b52a5)

## Build Source Code

```cmd
./gradlew jar
```
### Install
    1. Open you Idea
    2. Open Plugin Setting
    3. Install Plugin For Disk
