package com.chabao18.springbootstarter.bootstrap;

import com.chabao18.rpc.RPCApplication;
import com.chabao18.rpc.config.RPCConfig;
import com.chabao18.rpc.config.RegistryConfig;
import com.chabao18.rpc.model.ServiceMetaInfo;
import com.chabao18.rpc.registry.LocalRegistry;
import com.chabao18.rpc.registry.Registry;
import com.chabao18.rpc.registry.RegistryFactory;
import com.chabao18.springbootstarter.annotation.RPCService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class ProviderBootStrap implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        RPCService rpcService = beanClass.getAnnotation(RPCService.class);
        if (rpcService != null) {
            Class<?> interfaceClass = rpcService.interfaceClass();
            if (interfaceClass == void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();

            LocalRegistry.register(serviceName, beanClass);

            final RPCConfig rpcConfig = RPCApplication.getRpcConfig();
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
