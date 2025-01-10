package com.chabao18.springbootstarter.annotation;


import com.chabao18.rpc.constant.RPCConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RPCReference {
    Class<?> interfaceClass() default void.class;

    String serviceVersion() default RPCConstant.DEFAULT_SERVICE_VERSION;

    boolean mock() default false;
}
