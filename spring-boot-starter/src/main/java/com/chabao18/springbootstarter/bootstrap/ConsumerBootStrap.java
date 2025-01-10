package com.chabao18.springbootstarter.bootstrap;

import com.chabao18.rpc.proxy.ServiceProxyFactory;
import com.chabao18.springbootstarter.annotation.RPCReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

public class ConsumerBootStrap implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Field[] declaredFields = beanClass.getDeclaredFields();
        for (Field field : declaredFields) {
            RPCReference rpcReference = field.getAnnotation(RPCReference.class);
            if (rpcReference != null) {
                // 为属性生成代理对象
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if (interfaceClass == void.class) {
                    interfaceClass = beanClass.getInterfaces()[0];
                }
                field.setAccessible(true);
                Object proxy = ServiceProxyFactory.getProxy(interfaceClass);
                try {
                    field.set(bean, proxy);
                    field.setAccessible(true);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("为字段注入代理对象失败", e);
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
