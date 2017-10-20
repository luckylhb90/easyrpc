package com.luckylhb.easyrpc.client.spring;

import com.luckylhb.easyrpc.client.proxy.RpcProxy;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Proxy;

/**
 * @Project : easyrpc
 * @Description : TODO
 * @Author : luckylhb
 * @Date : 2017/10/1
 */
public class SpringProxyFactoryBean<T> implements InitializingBean, FactoryBean<T> {

    private static final Logger logger = LoggerFactory.getLogger(SpringProxyFactoryBean.class);

    @Setter
    private String innerClassName;
    private int timeoutInMillis;

    public T getObject() throws ClassNotFoundException {
        Class innerClass = Class.forName(innerClassName);
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{innerClass}, new RpcProxy());
    }

    public Class<?> getObjectType() {
        try {
            return Class.forName(innerClassName);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }


    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
