package com.chabao18.rpc.loadbalancer;

import com.chabao18.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RRLoadBalancer implements LoadBalancer{
    private static AtomicInteger currentID = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(String requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }

        int size = serviceMetaInfoList.size();
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }

        int id = currentID.getAndIncrement() % size;
        return serviceMetaInfoList.get(id);
    }
}
