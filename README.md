# springboot-invoke-plugin

[中文](https://github.com/houxinlin/springboot-invoke-plugin/blob/main/README.zh.md)

It is used to solve the call of the Controller method and the regular timer method. It is very convenient.

## Steps for usage:

1. Install it
2. Select SpringBeanInvoke in View → tool window
3. Restart your project

After the project is started, all timer and controller methods will be displayed. 

## Question

1. What is the proxy object and the original object?
  
    Answer: This plug-in does not call the Controller through HTTP requests, but internally through reflection. Therefore, when obtaining objects, the objects may be proxied by CGLIB, but you can choose the original objects, but some AOPs may not be called.


2. What is an interceptor?

    If your project has an interceptor that hits this Controller, when you choose to apply the interceptor, this interceptor will be called first. If it is not selected, even if the interceptor hits this Controller, it will not be called. This is the power of this plugin In order to debug the Controller when there is no authentication

# screenshot

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
