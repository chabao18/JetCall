package com.chabao18.rpc.model;

import com.chabao18.rpc.constant.RPCConstant;
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

    private String serviceVersion = RPCConstant.DEFAULT_SERVICE_VERSION;

    private Class<?>[] parameterTypes;

    private Object[] args;
}
