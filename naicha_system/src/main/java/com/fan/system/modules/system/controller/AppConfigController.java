package com.fan.system.modules.system.controller;

import com.fan.common.common.annotation.NeedPermission;
import com.fan.common.common.constant.Const;
import com.fan.common.common.exception.ServiceException;
import com.fan.common.common.util.result.Result;
import com.fan.common.entity.common.AppConfig;
import com.fan.common.service.AppConfigService;
import com.fan.common.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "系统：小程序配置")
@RestController
@RequestMapping("/config")
public class AppConfigController {

    @Resource
    private AppConfigService appConfigService;
    @Resource
    private RedisService redisService;

    @ApiOperation("获取小程序的所有配置")
    @GetMapping
    public Result<AppConfig> getAppConfig() throws ServiceException {
        return Result.ok(appConfigService.getAppConfig());
    }

    @ApiOperation("修改配置信息")
    @NeedPermission("system:admin:config:update")
    @PutMapping
    public Result<AppConfig> updateAppConfig(@RequestBody AppConfig updateConfig) throws ServiceException {
        if (updateConfig.getBusinessStartTime() < 0 || updateConfig.getBusinessStartTime() > 24
                || updateConfig.getBusinessEndTime() < 0 || updateConfig.getBusinessEndTime() > 24)
            throw new ServiceException("营业开始时间或结束时间不合法, 只能为0到24的整数");
        if (updateConfig.getBusinessStartTime() >= updateConfig.getBusinessEndTime())
            throw new ServiceException("请将营业开始时间设置在营业结束时间之前, [开始时间, 结束时间)");

        redisService.set(Const.CONST_app_config, updateConfig); // 刷新到缓存里
        return getAppConfig();
    }

}
