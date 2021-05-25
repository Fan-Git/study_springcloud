package com.fan.app.moudles.controller;

import com.fan.common.entity.app.vo.GoodsMenuVO;
import com.fan.app.moudles.service.impl.GoodsServiceImpl;
import com.fan.common.common.util.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "商品服务")
@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Resource
    private GoodsServiceImpl goodsService;
    @Resource
    private RestTemplate restTemplate;

    public static final String NAICHA_GOODS_SERVICE= "http://NAICHA-GOODS-SERVICE";

    @ApiOperation("获取要显示的商品菜单列表")
    @GetMapping("/goodsMenu/list")
    public Result<List<GoodsMenuVO>> getGoodsMenuDetailList() {
        return restTemplate.getForObject(NAICHA_GOODS_SERVICE+"/goods/goodsMenu/list",Result.class) ;
    }

}

