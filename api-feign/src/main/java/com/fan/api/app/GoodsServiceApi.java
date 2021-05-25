package com.fan.api.app;

import com.fan.api.app.hystrix.GoodsServiceApiHystrix;
import com.fan.common.common.util.result.Result;
import com.fan.common.entity.app.vo.GoodsMenuVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "naicha-goods-service", fallback = GoodsServiceApiHystrix.class)
public interface GoodsServiceApi {

    // 获取要显示的商品菜单列表
    @GetMapping("/goods/goodsMenu/list")
    public Result<List<GoodsMenuVO>> getGoodsMenuDetailList();
}
