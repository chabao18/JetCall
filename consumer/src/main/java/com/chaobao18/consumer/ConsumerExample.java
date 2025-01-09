package com.chaobao18.consumer;

import com.chabao18.common.model.User;
import com.chabao18.common.service.UserService;
import com.chabao18.rpc.config.RPCConfig;
import com.chabao18.rpc.proxy.ServiceProxyFactory;
import com.chabao18.rpc.utils.ConfigUtils;

public class ConsumerExample {
    public static void main(String[] args) {

        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setName("chabao18 - Test " + i);

            User newUser = userService.getUser(user);
            System.out.println("Request " + i + ":");
            if (newUser != null) {
                System.out.println("Response: " + newUser.getName());
            } else {
                System.out.println("Response: user == null");
            }
        }
    }
}
