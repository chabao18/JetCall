package com.chabao18.rpc.loadbalancer;

import com.chabao18.rpc.model.ServiceMetaInfo;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashLoadBalancer implements LoadBalancer{
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();
    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(String requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }

        // 构建虚拟节点环
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                int hash = hash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        int hash = hash(requestParams);
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null) {
            entry = virtualNodes.firstEntry();
        }

        return entry.getValue();
    }

    private int hash(String key) {
        byte[] data = key.getBytes(StandardCharsets.UTF_8);
        int length = data.length;
        int seed = 0x9747b28c; // 种子值，确保一致性
        int m = 0x5bd1e995;
        int r = 24;

        int h = seed ^ length;
        int i = 0;
        while (length >= 4) {
            int k = ((data[i] & 0xFF))
                    | ((data[i + 1] & 0xFF) << 8)
                    | ((data[i + 2] & 0xFF) << 16)
                    | ((data[i + 3] & 0xFF) << 24);

            k *= m;
            k ^= k >>> r;
            k *= m;

            h *= m;
            h ^= k;

            i += 4;
            length -= 4;
        }

        switch (length) {
            case 3:
                h ^= (data[i + 2] & 0xFF) << 16;
            case 2:
                h ^= (data[i + 1] & 0xFF) << 8;
            case 1:
                h ^= (data[i] & 0xFF);
                h *= m;
        }

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return (h & 0x7FFFFFFF) % Integer.MAX_VALUE;
    }
}
