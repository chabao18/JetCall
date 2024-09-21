package com.chabao18.rpc.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class RPCRequest implements Serializable {
    private String serviceName;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] args;
}
