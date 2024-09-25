package com.chabao18.rpc.model;

public class ServiceMetaInfo {
    private String serviceName;

    private String serviceVersion = "1.0";

    private String serviceHost;

    private String servicePort;

    private String serviceGroup = "default";

    public String getServiceKey() {
        return String.format("%s:%s:%s", serviceName, serviceVersion, serviceGroup);
    }

    public String getServiceNodeKey() {
        return String.format("%s:%s:%s", getServiceKey(), serviceHost, servicePort);
    }
}
