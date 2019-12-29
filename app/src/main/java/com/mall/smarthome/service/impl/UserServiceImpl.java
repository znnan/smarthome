package com.mall.smarthome.service.impl;

import com.mall.smarthome.common.ServerResponse;
import com.mall.smarthome.dao.UserMapper;
import com.mall.smarthome.pojo.User;
import com.mall.smarthome.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password){
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("User doesn't exist");
        }

        //TODO MD5 salt

        User user = userMapper.selectLogin(username, password);
        if(user == null){
            return ServerResponse.createByErrorMessage("password is incorrect");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("Login successfully", user);
    }
}
