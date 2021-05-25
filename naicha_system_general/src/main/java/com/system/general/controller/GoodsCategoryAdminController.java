package com.system.general.controller;

import com.fan.common.common.annotation.NeedPermission;
import com.fan.common.common.util.result.Result;
import com.fan.common.entity.app.GoodsCategory;
import com.system.general.mapper.GoodsCategoryAdminMapper;
import com.system.general.service.GoodsCategoryAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "系统：商品类别管理")
@RestController
@RequestMapping("/goodsCategoryAdmin")
public class GoodsCategoryAdminController {

    @Resource
    private GoodsCategoryAdminService goodsCategoryAdminService;
    @Resource
    private GoodsCategoryAdminMapper goodsCategoryAdminMapper;

    @ApiOperation("查询全部")
    @NeedPermission("system:app:goodsCategory:list")
    @GetMapping("/list")
    public Result<List<GoodsCategory>> getAllGoodsCategoryAdmins() {
        return Result.ok(goodsCategoryAdminService.getAllGoodsCategoryAdmins());
    }

    @ApiOperation("增加")
    @NeedPermission("system:app:goodsCategory:add")
    @PostMapping("")
    public Result<Integer> add(@RequestBody GoodsCategory goodsCategoryAdmin) {
        return Result.ok(goodsCategoryAdminService.addGoodsCategoryAdmin(goodsCategoryAdmin));
    }

    @ApiOperation("批量删除")
    @NeedPermission("system:app:goodsCategory:delete")
    @DeleteMapping("/{categoryName}")
    public Result<Integer> delete(@PathVariable String categoryName) {
        return Result.ok(goodsCategoryAdminService.deleteGoodsCategoryAdminByName(categoryName));
    }

    @ApiOperation("修改")
    @NeedPermission("system:app:goodsCategory:update")
    @PutMapping("/{oldName}")
    public Result<Integer> update(@PathVariable String oldName, @RequestBody GoodsCategory goodsCategoryAdmin) {
        return Result.ok(goodsCategoryAdminService.updateGoodsCategoryAdmin(oldName, goodsCategoryAdmin));
    }

    @ApiOperation("显示或隐藏该类商品")
    @NeedPermission("system:app:goodsCategory:update")
    @PutMapping("/showStatus/{name}")
    public Result<Integer> updateCategoryShowStatus(@PathVariable String name) {
        return Result.ok(goodsCategoryAdminMapper.updateCategoryShowStatus(name));
    }


}
