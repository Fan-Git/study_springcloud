package com.fan.goods.service;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fan.common.common.exception.ServiceException;
import com.fan.common.entity.app.dto.GoodsDTO;
import com.fan.common.entity.app.vo.GoodsMenuVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GoodsService {

    // 获取要显示的商品菜单列表  List<GoodsMenuVO>
    List<GoodsMenuVO>  getGoodsMenuDetailList();
    //分页查取商品信息
    public Page<GoodsDTO> getGoodsAdminByPage(int pageNo, int pageSize);
    // 根据id查询商品
    public GoodsDTO getGoodsById(Integer goodsId);
    // 增加
    public int addGoodsAdmin(GoodsDTO goodsDTO);
    // 批量删除
    public int deleteGoodsAdminBatchIds(List<Integer> goodsAdminIdList);
    // 修改
    public int updateGoodsAdmin(GoodsDTO goods);
    // 修改商品的图片
    public int updateGoodsImage(Integer goodsId, MultipartFile file) throws ServiceException;
}
