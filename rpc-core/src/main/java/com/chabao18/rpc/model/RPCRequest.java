package com.chabao18.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RPCRequest implements Serializable {
    private String serviceName;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] args;
}
