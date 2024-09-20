package com.chaobao18.consumer;

import com.chabao18.common.model.User;
import com.chabao18.common.service.UserService;

public class ConsumerExample {
    public static void main(String[] args) {
        // todo get UserService instance
        UserService userService = null;
        User user = new User();
        user.setName("chabao18");

        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
