package com.chabao18.provider;

import com.chabao18.common.model.User;
import com.chabao18.common.service.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("User name: " + user.getName());
        return user;
    }
}
