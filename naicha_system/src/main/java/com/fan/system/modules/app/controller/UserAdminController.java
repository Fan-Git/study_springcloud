package com.fan.system.modules.app.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fan.common.common.annotation.NeedPermission;
import com.fan.common.common.util.result.Result;
import com.fan.common.entity.app.User;

import com.fan.system.modules.app.service.UserAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "系统：用户管理")
@RestController
@RequestMapping("/userAdmin")
public class UserAdminController {

    @Resource
    private UserAdminService userAdminService;

    @ApiOperation("查询全部")
    @NeedPermission("system:app:user:list")
    @GetMapping("/list")
    public Result<List<User>> getAllUserAdmins() {
        return Result.ok(userAdminService.getAllUserAdmins());
    }

    @ApiOperation("分页查询")
    @NeedPermission("system:app:user:list")
    @GetMapping("/page")
    public Result<Page<User>> getuserAdminByPage(@RequestParam(defaultValue = "1") int pageNo,
                                                      @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(userAdminService.getUserAdminByPage(pageNo, pageSize));
    }

    @ApiOperation("增加")
    @NeedPermission("system:app:user:add")
    @PostMapping("")
    public Result<Integer> add(@RequestBody User userAdmin) {
        return Result.ok(userAdminService.addUserAdmin(userAdmin));
    }

    @ApiOperation("批量删除")
    @NeedPermission("system:app:user:delete")
    @DeleteMapping("/batch")
    public Result<Integer> delete(@RequestBody List<Integer> idList) {
        return Result.ok(userAdminService.deleteUserAdminBatchIds(idList));
    }

    @ApiOperation("修改")
    @NeedPermission("system:app:user:update")
    @PutMapping("")
    public Result<Integer> update(@RequestBody User userAdmin) {
        return Result.ok(userAdminService.updateUserAdmin(userAdmin));
    }
}
