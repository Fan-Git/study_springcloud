package com.fan.app.moudles.service;



import com.fan.common.entity.app.vo.GoodsMenuVO;

import java.util.List;

public interface GoodsService {

    // 获取要显示的商品菜单列表  List<GoodsMenuVO>
    List<GoodsMenuVO>  getGoodsMenuDetailList();

}
