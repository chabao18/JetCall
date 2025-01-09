package com.chabao18.rpc.loadbalancer;

import com.chabao18.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

public interface LoadBalancer {
    ServiceMetaInfo select(String requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
