# springboot-invoke-plugin

This is an exciting plug-in

It is used to solve the call of the Controller method and the regular timer method. It is very convenient.

## Steps for usage:

1. Install it
2. Select SpringBeanInvoke in View → tool window
3. Restart your project

After the project is started, all timer and controller methods will be displayed. Double-click to call.

## Question

1. What is the proxy object and the original object?
  
    Answer: This plug-in does not call the Controller through HTTP requests, but internally through reflection. Therefore, when obtaining objects, the objects may be proxied by CGLIB, but you can choose the original objects, but some AOPs may not be called.


2. What is an interceptor?

    If your project has an interceptor that hits this Controller, when you choose to apply the interceptor, this interceptor will be called first. If it is not selected, even if the interceptor hits this Controller, it will not be called. This is the power of this plugin In order to debug the Controller when there is no authentication

# screenshot

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
