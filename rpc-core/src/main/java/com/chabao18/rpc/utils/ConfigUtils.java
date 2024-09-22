package com.chabao18.rpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

public class ConfigUtils {
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    public static <T> T loadConfig(Class<T> tClass, String prefix, String env) {
        StringBuilder configBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(env)) {
            configBuilder.append("-").append(env);
        }
        configBuilder.append(".properties");
        Props props = new Props(configBuilder.toString());
        return props.toBean(tClass, prefix);
    }

}
