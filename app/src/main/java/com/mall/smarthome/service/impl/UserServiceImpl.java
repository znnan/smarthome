package com.mall.smarthome.service.impl;

import com.mall.smarthome.common.Const;
import com.mall.smarthome.common.ServerResponse;
import com.mall.smarthome.common.TokenCache;
import com.mall.smarthome.dao.UserMapper;
import com.mall.smarthome.pojo.User;
import com.mall.smarthome.service.IUserService;
import com.mall.smarthome.util.MD5Util;
import jdk.nashorn.internal.parser.Token;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

        String md5Password = MD5Util.MD5EncodeUtf8(password);

        User user = userMapper.selectLogin(username, md5Password);
        if(user == null){
            return ServerResponse.createByErrorMessage("password is incorrect");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("Login successfully", user);
    }

    public ServerResponse register(User user){
        ServerResponse validResponse = this.checkValid(user.getUsername(), Const.USER_NAME);
        if (validResponse.isSuccess()){
            return validResponse;
        }

        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (validResponse.isSuccess()){
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);

        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("register failed");
        }

        return ServerResponse.createBySuccessMessage("register success");
    }

    public ServerResponse<String> checkValid(String str, String type){
        if (org.apache.commons.lang3.StringUtils.isBlank(type)){
            if (Const.USER_NAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("User name is existing");
                }
            }
            if (Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMessage("Email is existing");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("parameter is wrong");
        }

        return ServerResponse.createBySuccessMessage("validation success");
    }

    public ServerResponse<String> selectQuestion(String username){
        ServerResponse validResponse = this.checkValid(username, Const.USER_NAME);
        if (validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("User doesn't exist");
        }

        String question = userMapper.selectQuestionByUserName(username);

        if(StringUtils.isNoneBlank(username)){
            return ServerResponse.createBySuccess(question);
        }

        return ServerResponse.createByErrorMessage("The question is empty");
    }

    public ServerResponse<String> checkAnswer(String username, String question, String answer){
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if(resultCount >0){
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("The answer is wrong");
    }

    public ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken){
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("parameter is wrong, token is needed");
        }

        ServerResponse validResponse = this.checkValid(username, Const.USER_NAME);
        if (validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("User doesn't exist");
        }

        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token is invalid or expired");
        }

        if (StringUtils.equals(forgetToken, token)){
            String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int rowCount = userMapper.updatePasswordByUserName(username, md5Password);

            if(rowCount > 0){
                return ServerResponse.createBySuccessMessage("new Password is set successfully");
            }
        } else {
            return ServerResponse.createByErrorMessage("token is invalid, please get the token once again");
        }

        return ServerResponse.createByErrorMessage("modification failed");
    }
}
