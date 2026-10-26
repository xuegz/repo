package com.atoken.update.dingding.annotaion.registrar;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InterfaceBeanFactory implements InvocationHandler {
    private String url;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        return null;
    }

    public static <T> T newInterfaceProxy(Class<T> mapperInterface) {
        ClassLoader classLoader = mapperInterface.getClassLoader();
        Class<?>[] interfaces = new Class[]{mapperInterface};
        InterfaceBeanFactory proxy = new InterfaceBeanFactory();
        return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
