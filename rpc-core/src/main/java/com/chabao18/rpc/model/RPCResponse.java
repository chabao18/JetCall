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
public class RPCResponse implements Serializable {
    private Object data;

    private Class<?> dataType;

    private String message;

    private Exception exception;
}
