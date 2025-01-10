package com.chabao18.springbootstarter.annotation;

import com.chabao18.rpc.constant.RPCConstant;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RPCService {
    Class<?> interfaceClass() default void.class;

    String serviceVersion() default RPCConstant.DEFAULT_SERVICE_VERSION;
}
