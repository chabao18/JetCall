package com.chabao18.springbootstarter.bootstrap;

import com.chabao18.rpc.RPCApplication;
import com.chabao18.rpc.config.RPCConfig;
import com.chabao18.rpc.server.VertxHttpServer;
import com.chabao18.springbootstarter.annotation.EnableRPC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;


@Slf4j
public class RPCInitBootStrap implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRPC.class.getName())
                .get("needServer");
        RPCApplication.init();

        final RPCConfig rpcConfig = RPCApplication.getRpcConfig();

        if (needServer) {
            VertxHttpServer httpServer = new VertxHttpServer();
            httpServer.doStart(rpcConfig.getServerPort());
        } else {
            log.info("不启动 server");
        }
    }
}
