package com.chabao18.rpc.config;

import lombok.Data;


@Data
public class RegistryConfig {
    private String registry = "etcd";

    private String address = "http://localhost:2380";

    private String username;

    private String password;

    private Long timeout = 10000L;
}
