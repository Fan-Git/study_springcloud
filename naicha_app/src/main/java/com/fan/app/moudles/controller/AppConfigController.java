package com.fan.app.moudles.controller;

import com.fan.common.common.exception.ServiceException;
import com.fan.common.common.util.result.Result;
import com.fan.common.entity.common.AppConfig;
import com.fan.common.service.AppConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "小程序配置")
@RestController
@RequestMapping("/config")
public class AppConfigController {

    @Resource
    private AppConfigService appConfigService;

    @ApiOperation("获取小程序的所有配置")
    @GetMapping
    public Result<AppConfig> getAppConfig() throws ServiceException {

        return Result.ok(appConfigService.getAppConfig());
    }
}
