# springboot-invoke-plugin

[中文](https://github.com/houxinlin/springboot-invoke-plugin/blob/main/README.zh.md)

It is used to solve the call of the Controller method and the regular timer method. It is very convenient.

## Steps for usage:

1. Install it
2. Select Cool Request in View → Tool window
3. Restart your project

After the project is started, all timer and controller methods will be displayed. 

## Features
<li>✓ Show all Controller information defined in Spring Boot, supporting HTTP/Reflection invocation</li>
<li>✓ Display timers defined in Spring Boot, manually triggerable without waiting for the specified time</li>
<li>✓ Bypass interceptors during requests</li>
<li>✓ Specify proxy/original objects during requests</li>
<li>✓ One-click export to openapi format</li>
<li>✓ One-click import to apifox</li>
<li>✓ Copy request as curl</li>
<li>✓ Compatible with gradle, maven multi-module projects</li>
<li>✓ Compatible with Java/Kotlin languages</li>
<li>✓ Powerful HTTP request parameter guessing, reducing developers' time to fill in keys</li>
<li>✓ Use Java syntax to handle pre/post request scripts</li>
<li>✓ Save response results to files</li>
<li>✓ Quickly preview json, xml, img, html, text responses</li>
<li>✓ Multiple layout switching</li>

## Question


1. What is the proxy object and the original object?
  
    Answer: This plug-in does not call the Controller through HTTP requests, but internally through reflection. Therefore, when obtaining objects, the objects may be proxied by CGLIB, but you can choose the original objects, but some AOPs may not be called.


2. What is an interceptor?

    If your project has an interceptor that hits this Controller, when you choose to apply the interceptor, this interceptor will be called first. If it is not selected, even if the interceptor hits this Controller, it will not be called. This is the power of this plugin In order to debug the Controller when there is no authentication



## Build Source Code

```cmd
./gradlew jar
```
### Install
    1. Open you Idea
    2. Open Plugin Setting
    3. Install Plugin For Disk
