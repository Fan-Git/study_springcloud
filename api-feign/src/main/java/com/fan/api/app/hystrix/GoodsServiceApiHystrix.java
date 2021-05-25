package com.fan.api.app.hystrix;

import com.fan.api.app.GoodsServiceApi;
import com.fan.common.common.util.result.Result;
import com.fan.common.entity.app.vo.GoodsMenuVO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GoodsServiceApiHystrix  implements GoodsServiceApi {
    //服务降级 返回null
    @Override
    public Result<List<GoodsMenuVO>> getGoodsMenuDetailList() {
        return Result.fail("商品服务不可用");
    }
}
