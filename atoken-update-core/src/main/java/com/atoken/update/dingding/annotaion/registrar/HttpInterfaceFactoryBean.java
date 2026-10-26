package com.atoken.update.dingding.annotaion.registrar;

import org.springframework.beans.factory.FactoryBean;


public class HttpInterfaceFactoryBean<T> implements FactoryBean<T> {
    private Class<T> httpInterface;

    public HttpInterfaceFactoryBean(Class<T> httpInterface) {
        this.httpInterface = httpInterface;
    }

    @Override
    public T getObject() {
        return InterfaceBeanFactory.newInterfaceProxy(this.httpInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return this.httpInterface;
    }

}
