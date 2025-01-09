package com.chabao18.rpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.chabao18.rpc.config.RegistryConfig;
import com.chabao18.rpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;

    private static final String ETCD_ROOT_PATH = "/rpc/";

    // the set of node keys registered, used for renewal
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    private final RegistryServiceCache cache = new RegistryServiceCache();

    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress()).connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        kvClient = client.getKVClient();
        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        Lease leaseClient = client.getLeaseClient();

        // create a 200s lease
        long leaseId = leaseClient.grant(200).get().getID();

        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // register
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();
        log.info("register K:{} - V{}", registerKey, JSONUtil.toJsonStr(serviceMetaInfo));

        // add service info to local cache
        localRegisterNodeKeySet.add(registerKey);

    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String unregisterKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        log.debug("unregister K:{} - V{}", unregisterKey, JSONUtil.toJsonStr(serviceMetaInfo));
        try {
            kvClient.delete(ByteSequence.from(unregisterKey, StandardCharsets.UTF_8)).get();
        } catch (Exception e) {
            log.error("failed to unregister service", e);
        }
        localRegisterNodeKeySet.remove(unregisterKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // get service from cache first
        List<ServiceMetaInfo> cachedServiceMetaInfoList = cache.readCache();
        if (cachedServiceMetaInfoList != null) {
            log.debug("serviceDiscovery hit cache");
            return cachedServiceMetaInfoList;
        }

        String searchPrefix = ETCD_ROOT_PATH + serviceKey;

        try {
            // prefix search
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(
                            ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                            getOption)
                    .get()
                    .getKvs();
            log.debug("found {} keys for prefix {}", keyValues.size(), searchPrefix);
            // 解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream()
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        // watch key
                        watch(key);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
            // write cache
            cache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("failed to get service list", e);
        }
    }

    @Override
    public void destroy() {
        for (String key : localRegisterNodeKeySet) {
            try {
                log.info("service offline: {}", key);
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                log.error("service offline failed: {}", e.getMessage());
                throw new RuntimeException(key + "offline failed");
            }
        }
        // 释放资源
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        // re-register every 3 minutes
        CronUtil.schedule("0 */3 * * * *", new Task() {
            @Override
            public void execute() {
                log.debug("start re-register process");
                for (String key : localRegisterNodeKeySet) {
                    try {
                        List<KeyValue> kvs = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        if (CollUtil.isEmpty(kvs)) {
                            continue;
                        }

                        // register again
                        KeyValue kv = kvs.get(0);
                        String value = kv.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        log.error("{} failed to re-register", key);
                        throw new RuntimeException(key + "failed to re-register", e);
                    }
                }
            }
        });

        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        if (newWatch) {
            watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8), response -> {
               for (WatchEvent event : response.getEvents()) {
                   switch (event.getEventType()) {
                       case DELETE:
                           cache.clearCache();
                           break;
                       case PUT:
                       default:
                           break;
                   }
               }
            });
        }
    }

}

