package com.mall.smarthome.service;

import com.mall.smarthome.common.ServerResponse;
import com.mall.smarthome.pojo.User;

/**
 * Create by Nan Zhao
 */
public interface IUserService {
   ServerResponse<User> login(String username, String password);

   ServerResponse<String> register(User user);

   ServerResponse<String> checkValid(String str, String type);
}
