package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    public static final String USER_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    public User login(UserLoginDTO userLoginDTO) {
        //调用微信官方Api获取用户openid'
        String openid = getOpenid(userLoginDTO.getCode());
        //如果没有返回openid，说明登录失败
        if (openid == null)
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);

        //查询用户表，判断是否是新用户，是则插入用户表
        User user = userMapper.selectByOpenid(openid);
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.save(user);
        }
        return user;
    }

    /**
     * 获得openid
     * @param code 授权码
     * @return
     */
    private String getOpenid(String code) {
        //调用微信官方Api获取用户openid'
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", weChatProperties.getAppid());
        paramMap.put("secret", weChatProperties.getSecret());
        paramMap.put("js_code", code);
        paramMap.put("grant_type", "authorization_code");
        String response = HttpClientUtil.doGet(USER_LOGIN_URL, paramMap);
        //解析返回数据（json格式），获取openid
        JSONObject jsonObject = JSON.parseObject(response);
        String openid = jsonObject.getString("openid");

        return openid;
    }

}
