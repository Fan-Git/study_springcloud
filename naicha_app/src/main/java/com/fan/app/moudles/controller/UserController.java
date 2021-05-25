package com.fan.app.moudles.controller;

import com.fan.common.entity.app.form.LoginByWeixinForm;
import com.fan.common.entity.app.form.UpdateUserForm;
import com.fan.app.moudles.service.impl.UserServiceImpl;
import com.fan.common.common.annotation.NeedLogin;
import com.fan.common.common.exception.ServiceException;
import com.fan.common.common.util.result.Result;
import com.fan.common.entity.app.User;
import com.fan.app.common.util.session.SessionUtil;
import com.fan.common.service.AppConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "用户服务")
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserServiceImpl userService;
    @Resource
    private AppConfigService appConfigService;

    // TODO
    @ApiOperation("开发测试时开发的登录接口")
    @PostMapping("/login/dev")
    public Result<User> login(String secretPassword) throws Exception {
        if (!"691567915".equals(secretPassword))
            throw ServiceException.CONST_login_failed;
        if(!appConfigService.getAppConfig().getTestUserLoginEnabled())
            throw ServiceException.CONST_test_login_has_closed;

        // 测试开发环境，必须事先到数据库里面去插入wxOpenid=0的数据
     //   User user = userService.getUser("oxci95SnSaCpN5NAvv66T4XYDVR8"); // 陈亚茹的微信的openid

        User user = userService.getUser("oxci95diR8PFRUrzBX19weF4qSsY");
        SessionUtil.setUserSession(user);
        log.info("[开发环境，用户登录] {}", user);
        return Result.ok(user);
    }

    @ApiOperation("通过微信登录")
    @PostMapping("/login/weixin")
    public Result<User> loginByWx(@RequestBody LoginByWeixinForm form) throws Exception {
        return Result.ok(userService.loginByWeixin(form));
    }

    @ApiOperation("退出登录")
    @NeedLogin
    @DeleteMapping("/logout")
    public Result logout(HttpServletRequest request)  {
        SessionUtil.logout(request);
        return Result.ok();
    }

    @ApiOperation("根据token获取用户数据, 并续期token")
    @NeedLogin
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) throws ServiceException {
        User user = userService.getUser(SessionUtil.getCurrentWxOpenidFromRequest(request));
        SessionUtil.setUserSession(user); // token已经更新
        return Result.ok(user);
    }

    @ApiOperation("更新信息")
    @NeedLogin
    @PutMapping
    public Result<Integer> updateUser(@ApiParam("更新学生信息表单") @RequestBody UpdateUserForm form, HttpServletRequest request) throws Exception {
        return Result.ok(userService.updateUser(form, SessionUtil.getCurrentWxOpenidFromRequest(request)));
    }
}
