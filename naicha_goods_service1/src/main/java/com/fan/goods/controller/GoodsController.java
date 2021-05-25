package com.fan.goods.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fan.common.common.annotation.NeedPermission;
import com.fan.common.common.exception.ServiceException;
import com.fan.common.common.util.result.Result;
import com.fan.common.entity.app.dto.GoodsDTO;
import com.fan.common.entity.app.vo.GoodsMenuVO;
import com.fan.goods.mapper.GoodsMapper;
import com.fan.goods.service.impl.GoodsServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Api(tags = "系统：商铺管理")
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private GoodsServiceImpl goodsService;

    @Value("${server.port}")
    private String port;
    @ApiOperation("获取要显示的商品菜单列表")
    @GetMapping("/goodsMenu/list")
    public Result<List<GoodsMenuVO>> getGoodsMenuDetailList() {

        log.info("我的端口号是"+port);
        return Result.ok(goodsService.getGoodsMenuDetailList());
    }

    @ApiOperation("分页查询")
    @NeedPermission("system:app:goods:list")
    @GetMapping("/page")
    public Result<Page<GoodsDTO>> getGoodsAdminByPage(@RequestParam(defaultValue = "1") int pageNo,
                                                      @RequestParam(defaultValue = "10") int pageSize) {
        log.info("我的端口号是"+port);
        return Result.ok(goodsService.getGoodsAdminByPage(pageNo, pageSize));
    }

    @ApiOperation("查询一个商品")
    @NeedPermission("system:app:goods:list")
    @GetMapping("/{goodsId}")
    public Result<GoodsDTO> getGoodsById(@PathVariable Integer goodsId) {
        return Result.ok(goodsService.getGoodsById(goodsId));
    }

    @ApiOperation("增加")
    @NeedPermission("system:app:goods:add")
    @PostMapping("")
    public Result<Integer> add(@RequestBody GoodsDTO goodsAdmin) {
        return Result.ok(goodsService.addGoodsAdmin(goodsAdmin));
    }

    @ApiOperation("批量删除")
    @NeedPermission("system:app:goods:delete")
    @DeleteMapping("/batch")
    public Result<Integer> delete(@RequestBody List<Integer> idList) {
        return Result.ok(goodsService.deleteGoodsAdminBatchIds(idList));
    }

    @ApiOperation("修改")
    @NeedPermission("system:app:goods:update")
    @PutMapping("")
    public Result<Integer> update(@RequestBody GoodsDTO goodsAdmin) {
        return Result.ok(goodsService.updateGoodsAdmin(goodsAdmin));
    }

    @ApiOperation("下架或上架商品")
    @NeedPermission("system:app:goods:update")
    @PutMapping("/updateSellStatus/{goodsId}")
    public Result<Integer> updateSellStatus(@PathVariable Integer goodsId) {
        return Result.ok(goodsMapper.updateSellStatus(goodsId));
    }

    @ApiOperation("更新商品的图片")
    @NeedPermission("system:app:goods:update")
    @RequestMapping("/image")
    public Result<Integer> updateGoodsImage(@RequestParam Integer goodsId, @RequestParam MultipartFile file) throws ServiceException { // file的名字要和前端input里的name一致
        return Result.ok(goodsService.updateGoodsImage(goodsId, file));
    }
}
