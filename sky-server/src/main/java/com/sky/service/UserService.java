package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

public interface UserService extends IService<User> {
    /*
     * 微信登录接口
     * */
    User wxLogin(UserLoginDTO userLoginDTO);
}
