package com.fan.app.moudles.service;


import com.fan.common.entity.app.form.LoginByWeixinForm;
import com.fan.common.entity.app.form.UpdateUserForm;
import com.fan.common.common.exception.ServiceException;
import com.fan.common.entity.app.User;

public interface UserService {

    // 通过微信小程序获取的code在服务器获取微信openid登录，没有就注册
    User loginByWeixin(LoginByWeixinForm form) throws ServiceException;

    // 获取学生详情信息
    User getUser(String wxOpenid);

    // 更新用户信息
    int updateUser(UpdateUserForm form, String wxOpenid) throws Exception;

}
