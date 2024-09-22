package com.chaobao18.consumer;

import com.chabao18.common.model.User;
import com.chabao18.common.service.UserService;
import com.chabao18.rpc.config.RPCConfig;
import com.chabao18.rpc.proxy.ServiceProxyFactory;
import com.chabao18.rpc.utils.ConfigUtils;

public class ConsumerExample {
    public static void main(String[] args) {

//        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
//        User user = new User();
//        user.setName("chabao18");
//
//        User newUser = userService.getUser(user);
//        if (newUser != null) {
//            System.out.println(newUser.getName());
//        } else {
//            System.out.println("user == null");
//        }
        RPCConfig config = ConfigUtils.loadConfig(RPCConfig.class, "rpc");
        System.out.println(config);
    }
}
