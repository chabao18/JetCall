package com.chabao18.springbootstarter.annotation;


import com.chabao18.springbootstarter.bootstrap.ConsumerBootStrap;
import com.chabao18.springbootstarter.bootstrap.ProviderBootStrap;
import com.chabao18.springbootstarter.bootstrap.RPCInitBootStrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RPCInitBootStrap.class, ProviderBootStrap.class, ConsumerBootStrap.class})
public @interface EnableRPC {
    boolean needServer() default true;
}
