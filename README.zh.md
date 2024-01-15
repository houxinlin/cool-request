<div align="center">
  <h1 align="center">
    Cool Request
    <br />
    <br />
    <a href="https://plugin.houxinlin.com">
      <img src="https://plugin.houxinlin.com/img/logo.svg" alt="Docusaurus">
    </a>
  </h1>
</div>

这个工具用于解决控制器方法和定时器方法的调用，非常便捷。

[文档](https://plugin.houxinlin.com)

# 使用步骤：
1. 安装插件
2. 在"View"菜单中选择Tool Windows >Cool Request
3. 重启你的项目
项目启动后，所有的定时器和Controller方法都将显示出来。

## 特性
- ✓️ 显示在Spring Boot中定义的所有控制器信息，支持HTTP/反射调用
- ✓ 显示在Spring Boot中定义的定时器，可手动触发而无需等待指定时间
- ✓ 请求期间绕过拦截器
- ✓ 在请求期间指定代理/原始对象
- ✓ 一键导出为OpenAPI格式
- ✓ 一键导入到Apifox
- ✓ 复制请求为curl
- ✓ 与Gradle、Maven多模块项目兼容
- ✓ 与Java/Kotlin语言兼容
- ✓ 强大的HTTP请求参数推测功能，减少开发者填写key的时间
- ✓ 使用Java语法编写请求前/请求后的脚本
- ✓ 将响应结果保存到文件
- ✓ 快速预览JSON、XML、图片、HTML、文本响应
- ✓ 多种布局切换
## 问题

1. 什么是代理对象和原始对象？

   答：该插件不通过HTTP请求调用Controller，而是通过反射在内部调用。因此，在获取对象时，对象可能被CGLIB代理，但你可以选择原始对象，但某些AOP可能在这个过程中失效。


2. 什么是拦截器？

   如果你的项目有一个拦截器与该Controller匹配，在选择应用拦截器时，该拦截器将首先被调用。如果未选择拦截器，即使拦截器匹配该Controller，也不会被调用。这是该插件诞生的初衷之一，用于在没有身份验证时调试Controller。



## Build Source Code

```cmd
./gradlew buildPlugin
```
### Install
    1. Open you Idea
    2. Open Plugin Setting
    3. Install Plugin For Disk
    4. 选择./build/distributions/cool-request-plugin.zip
