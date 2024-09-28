package com.chabao18.rpc.registry;

import cn.hutool.json.JSONUtil;
import com.chabao18.rpc.config.RegistryConfig;
import com.chabao18.rpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;

    /**
     * 根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress()).connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        kvClient = client.getKVClient();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 创建 Lease 和 KV 客户端
        Lease leaseClient = client.getLeaseClient();

        // 创建一个 30 秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        // 设置要存储的键值对
        // fix bug
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 将键值对与租约关联起来，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();
        log.info("register K:{} - V{}", registerKey, JSONUtil.toJsonStr(serviceMetaInfo));

    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String unregisterKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        log.info("unregister K:{} - V{}", unregisterKey, JSONUtil.toJsonStr(serviceMetaInfo));
        try {
            kvClient.delete(ByteSequence.from(unregisterKey, StandardCharsets.UTF_8)).get();
        } catch (Exception e) {
            log.error("Failed to unregister service", e);
        }
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 前缀搜索，结尾一定 【不要！】 要加 '/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey;
        log.info("search key: {}", searchPrefix);

        try {
            // 前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(
                            ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                            getOption)
                    .get()
                    .getKvs();
            log.info("Found {} keys for prefix {}", keyValues.size(), searchPrefix);
            // 解析服务信息
            return keyValues.stream()
                    .map(keyValue -> {
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        // 释放资源
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

}

