package com.chabao18.rpc.registry;

import com.chabao18.rpc.config.RegistryConfig;
import com.chabao18.rpc.model.ServiceMetaInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ZooKeeperRegistry implements Registry{
    @Override
    public void init(RegistryConfig registryConfig) {

    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {

    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {

    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        return null;
    }

    @Override
    public void destroy() {

    }

    public static void main(String[] args) throws Exception {
        // 1. 建立 Curator 连接
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                "localhost:2181", new ExponentialBackoffRetry(1000, 3));
        client.start();

        // 2. 定义服务实例
        ServiceInstance<Object> serviceInstance = ServiceInstance.builder()
                .name("example-service")
                .port(8080)
                .address("localhost")
                .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                .build();

        // 3. 创建服务发现
        ServiceDiscovery<Object> serviceDiscovery = ServiceDiscoveryBuilder.builder(Object.class)
                .client(client)
                .basePath("/services")  // 在 Zookeeper 中的基础路径
                .build();

        // 4. 注册服务
        serviceDiscovery.registerService(serviceInstance);
        serviceDiscovery.start();

        System.out.println("Service registered: " + serviceInstance);

        // 保持服务运行一段时间以便验证
        TimeUnit.MINUTES.sleep(3);

        // 关闭服务发现和 Zookeeper 客户端
        serviceDiscovery.close();
        client.close();
        System.out.println("client close");
    }
}
