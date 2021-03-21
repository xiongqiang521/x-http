package com.xq.xhttp.http.handler;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class HttpApiFactionBean<T> implements FactoryBean<T> {

    private final Class<T> interfaceClass;

    public HttpApiFactionBean(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public T getObject() throws Exception {
        HttpApiProxy<T> httpApiProxy = new HttpApiProxy<>();
        Object o = Proxy.newProxyInstance(
                this.interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                httpApiProxy::invoke
        );
        return (T) o;
    }

    @Override
    public Class<T> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
