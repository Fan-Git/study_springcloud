package com.fan.app.moudles.service.impl;

import com.fan.app.common.util.session.SessionUtil;
import com.fan.app.moudles.mapper.UserMapper;
import com.fan.app.moudles.service.UserService;
import com.fan.common.entity.app.form.LoginByWeixinForm;
import com.fan.common.entity.app.form.UpdateUserForm;
import com.fan.common.entity.app.User;
import com.fan.common.common.exception.ServiceException;
import com.fan.common.service.RedisService;
import com.fan.common.service.WeixinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private WeixinService weixinService;

    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisService redisService;


    // 通过微信登录，没有就注册
    @Transactional
    @Override
    public User loginByWeixin(LoginByWeixinForm form) throws ServiceException {
        log.warn("登录传入的参数为: " + form.toString());
        String wxOpenid = weixinService.getWeiXinOpenid(form.getCode());
        if (wxOpenid != null) {
            User user = userMapper.selectById(wxOpenid);
            if (user == null) { // 没有就注册
                user = new User();
                user.setWxOpenid(wxOpenid);
                user.setStatus(true);
                userMapper.insert(user);
            }
            if (!user.getStatus()) { // 检查账号是否冻结
                throw ServiceException.CONST_user_is_forbidden;
            }

            user.setWxAvatar(form.getWxAvatar()); // 微信头像
            SessionUtil.setUserSession(user);
            userMapper.updateWxAvatar(user.getWxAvatar(), wxOpenid);
            log.info("[通过微信登录] {}", user);
            return user;
        } else {
            throw ServiceException.CONST_wx_login_failed;
        }
    }

    @Override
    public User getUser(String userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public int updateUser(UpdateUserForm form, String wxOpenid) throws Exception {
        User user = new User();
        user.setWxOpenid(wxOpenid);
        user.setName(form.getName());
        user.setAddress(form.getAddress());
        user.setWxAvatar(form.getWxAvatar());
        user.setPhone(form.getPhone());
        user.setSex(form.getSex());
        return userMapper.updateById(user);
    }
}
