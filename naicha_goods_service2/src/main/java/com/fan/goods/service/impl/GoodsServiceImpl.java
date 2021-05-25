package com.fan.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fan.common.common.config.property.IOProperty;
import com.fan.common.common.constant.Const;
import com.fan.common.common.enums.GoodsPropertyCategory;
import com.fan.common.common.exception.ServiceException;
import com.fan.common.entity.app.Goods;
import com.fan.common.entity.app.GoodsCategory;
import com.fan.common.entity.app.GoodsProperty;
import com.fan.common.entity.app.dto.GoodsDTO;
import com.fan.common.entity.app.vo.GoodsMenuVO;
import com.fan.common.entity.app.vo.GoodsVO;
import com.fan.common.entity.app.vo.SameCategoryPropertyVO;
import com.fan.common.service.RedisService;
import com.fan.goods.mapper.GoodsAppMapper;
import com.fan.goods.mapper.GoodsCategoryMapper;
import com.fan.goods.mapper.GoodsMapper;
import com.fan.goods.mapper.GoodsPropertyMapper;
import com.fan.goods.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

@Slf4j
@Service
@ComponentScans({@ComponentScan("com.fan.common.common.config")})
public class GoodsServiceImpl implements GoodsService {


    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private GoodsAppMapper goodsAppMapper;
    @Resource
    private GoodsPropertyMapper goodsPropertyMapper;
    @Resource
    private IOProperty ioProperty;
    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;
    @Resource
    private RedisService redisService;

    // TODO 本地商品菜单缓存
    private List<GoodsMenuVO> goodsMenuVOSLocalCache = new ArrayList<>();
    
    @Override
    public List<GoodsMenuVO> getGoodsMenuDetailList() {
        Object o = redisService.get(Const.CONST_goods_menu_vo_cache);
        if (o != null && !CollectionUtils.isEmpty(goodsMenuVOSLocalCache))
            return goodsMenuVOSLocalCache;

        List<GoodsCategory> goodsCategoryList = goodsCategoryMapper.selectList(null);
        // 使用2个map去关联关系
        HashMap<String, List<Goods>> sameCategoryGoodsMap = new HashMap<>(goodsCategoryList.size());
        HashMap<String, GoodsMenuVO> goodsMenuVOMap = new HashMap<>(goodsCategoryList.size());
        for (GoodsCategory goodsCategory : goodsCategoryList) {
            sameCategoryGoodsMap.put(goodsCategory.getName(), new ArrayList<>());
            GoodsMenuVO goodsMenuVO = new GoodsMenuVO();
            goodsMenuVO.setGoodsCategoryName(goodsCategory.getName());
            goodsMenuVO.setGoodsCategoryName(goodsCategory.getName());
            goodsMenuVO.setDisplayOrder(goodsCategory.getDisplayOrder());
            goodsMenuVO.setGoodsCategoryShow(goodsCategory.getShowStatus());
            goodsMenuVOMap.put(goodsCategory.getName(), goodsMenuVO);
        }

        List<Goods> allGoods = goodsAppMapper.selectList(null);
        // 将所有商品分类
        for (Goods good : allGoods)
            if (sameCategoryGoodsMap.containsKey(good.getGoodsCategoryName()))
                sameCategoryGoodsMap.get(good.getGoodsCategoryName()).add(good);

        Random random = new Random(10000);
        // 关联同类商品排好序后放到到对应的类别里
        for (Map.Entry<String, GoodsMenuVO> goodsMenuVOEntry : goodsMenuVOMap.entrySet()) {
            List<Goods> sameCategoryGoodsList = sameCategoryGoodsMap.get(goodsMenuVOEntry.getKey());
            sameCategoryGoodsList.sort((o1, o2) -> o1.getDisplayOrder() - o2.getDisplayOrder());
            List<GoodsVO> goodsVos = new ArrayList<>();
            // 填充商品的属性
            for (Goods goods : sameCategoryGoodsList) {
                List<GoodsProperty> goodsPropertyList = goodsPropertyMapper.selectList(
                        new QueryWrapper<GoodsProperty>().eq("goods_id", goods.getId()));
                HashMap<String, List<GoodsProperty>> propertyMap = new HashMap<>();
                for (GoodsProperty goodsProperty : goodsPropertyList) {
                    if (propertyMap.containsKey(goodsProperty.getCategory())) {
                        propertyMap.get(goodsProperty.getCategory()).add(goodsProperty);
                    } else {
                        propertyMap.put(goodsProperty.getCategory(), new ArrayList<GoodsProperty>() {{
                            add(goodsProperty);
                        }});
                    }
                    if (goodsProperty.getIsDefault() && GoodsPropertyCategory.ENUM_size.value.equals(goodsProperty.getCategory()))
                        goods.setDefaultPrice(goodsProperty.getRebasePrice()); // 重新设置商品的默认价格
                }
                List<SameCategoryPropertyVO> goodsPropertyVos = new ArrayList<>();
                for (Map.Entry<String, List<GoodsProperty>> entry : propertyMap.entrySet()) {
                    // 属性类别->属性列表  转换成对象
                    SameCategoryPropertyVO goodsPropertyVo = new SameCategoryPropertyVO();
                    goodsPropertyVo.setCategory(entry.getKey());
                    goodsPropertyVo.setRequired(!GoodsPropertyCategory.ENUM_jia_liao.value.equalsIgnoreCase(entry.getKey())); // 除了加料其他全部必选
                    goodsPropertyVo.setPropertyList(entry.getValue());
                    goodsPropertyVos.add(goodsPropertyVo);
                }
                GoodsVO goodsVo = new GoodsVO();
                BeanUtils.copyProperties(goods, goodsVo);
                goodsVo.setGoodsPropertyVos(goodsPropertyVos);
                goodsVo.setRealPrice(goods.getDefaultPrice());
                goodsVo.setImage(goodsVo.getImage() + "?random=" + random.nextInt()); // 动态刷新小程序里的图片
                goodsVos.add(goodsVo);
            }
            goodsMenuVOEntry.getValue().setGoodsList(goodsVos);
        }

        List<GoodsMenuVO> goodsMenuVOList = new ArrayList<>(goodsMenuVOMap.values());
        // 最后对菜单进行排序
        goodsMenuVOList.sort((o1, o2) -> o1.getDisplayOrder() - o2.getDisplayOrder());
        redisService.set(Const.CONST_goods_menu_vo_cache, true, Const.CONST_one_hour); // 失效时间
        return this.goodsMenuVOSLocalCache = goodsMenuVOList;
    }
    /**
     * 分页条件查询
     *
     * @param pageNo   页号
     * @param pageSize 页面大小
     * @return Page<GoodsAdmin>
     */
    public Page<GoodsDTO> getGoodsAdminByPage(int pageNo, int pageSize) {
        Page<GoodsDTO> page = new Page<>(pageNo, pageSize);
        goodsMapper.selectPage(page, new QueryWrapper<GoodsDTO>().orderByAsc("goods_category_name"));
        List<GoodsDTO> goodsVOList = page.getRecords();
        for (GoodsDTO goods : goodsVOList) {
            // 设置商品的属性列表
            List<GoodsProperty> goodsPropertyList = goodsPropertyMapper
                    .selectList(new QueryWrapper<GoodsProperty>().eq("goods_id", goods.getId()));
            goods.setGoodsPropertyList(goodsPropertyList);

        }
        return page;
    }

    // 根据id查询商品
    public GoodsDTO getGoodsById(Integer goodsId) {
        GoodsDTO goods = goodsMapper.selectById(goodsId);
        goods.setGoodsPropertyList(
                goodsPropertyMapper
                        .selectList(new QueryWrapper<GoodsProperty>().eq("goods_id", goodsId))
        );
        return goods;
    }

    // 增加
    @Transactional
    public int addGoodsAdmin(GoodsDTO goodsDTO) {
        return goodsMapper.insert(goodsDTO);
    }

    // 批量删除
    @Transactional
    public int deleteGoodsAdminBatchIds(List<Integer> goodsAdminIdList) {
        return goodsMapper.deleteBatchIds(goodsAdminIdList);
    }

    // 修改
    @Transactional
    public int updateGoodsAdmin(GoodsDTO goods) {
        return goodsMapper.updateById(goods);
    }

    // 修改商品的图片
    public int updateGoodsImage(Integer goodsId, MultipartFile file) throws ServiceException {
        try {
            File dir = new File(ioProperty.getImageFileRootPath());
            if (!dir.exists()) // 不存在该目录就创建
                dir.mkdir();
            String goodsImageName = "goodsImage-" + goodsId + ".jpg";
            file.transferTo(new File(dir, goodsImageName));
            if (file.getSize() > 1024 * 1024)
                throw ServiceException.CONST_goods_image_upload_failed; // 文件超过1MB

            return goodsMapper.updateImageByGoodsId(goodsId, goodsImageName);
        } catch (Exception e) {
            throw ServiceException.CONST_goods_image_upload_failed;
        }
    }

}
