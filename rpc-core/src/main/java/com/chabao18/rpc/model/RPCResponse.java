package com.chabao18.rpc.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class RPCResponse implements Serializable {
    private Object data;

    private Class<?> dataType;

    private String message;

    private Exception exception;
}
