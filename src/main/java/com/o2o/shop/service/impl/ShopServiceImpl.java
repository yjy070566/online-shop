package com.o2o.shop.service.impl;

import com.o2o.shop.bean.Shop;
import com.o2o.shop.bean.ShopAuthMap;
import com.o2o.shop.bean.ShopCategory;
import com.o2o.shop.dto.ShopExecution;
import com.o2o.shop.enums.ShopStateEnum;
import com.o2o.shop.mapper.ShopAuthMapMapper;
import com.o2o.shop.mapper.ShopCategoryMapper;
import com.o2o.shop.mapper.ShopMapper;
import com.o2o.shop.service.ShopService;
import com.o2o.shop.util.FileUtils;
import com.o2o.shop.util.ImageUtils;
import com.o2o.shop.util.PageCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.Date;
import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private ShopAuthMapMapper shopAuthMapMapper;
    @Autowired
    private ShopCategoryMapper shopCategoryMapper;

    @Override
    public ShopExecution getShopList(Shop shopCondition, int pageIndex,
                                     int pageSize) {
        int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
        List<Shop> shopList = shopMapper.queryShopList(shopCondition, rowIndex,
                pageSize);
        int count = shopMapper.queryShopCount(shopCondition);
        ShopExecution se = new ShopExecution();
        if (shopList != null) {
            se.setShopList(shopList);
            se.setCount(count);
        } else {
            se.setState(ShopStateEnum.INNER_ERROR.getState());
        }
        return se;
    }

    @Override
    public ShopExecution getByEmployeeId(long employeeId)
            throws RuntimeException {
        List<Shop> shopList = shopMapper.queryByEmployeeId(employeeId);
        ShopExecution se = new ShopExecution();
        se.setShopList(shopList);
        return se;
    }

    @Override
    public Shop getByShopId(long shopId) {
        return shopMapper.queryByShopId(shopId);
    }

    @Override
    @Transactional
    /**
     * 使用注解控制事务方法的优点： 1.开发团队达成一致约定，明确标注事务方法的编程风格
     * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作，RPC/HTTP请求或者剥离到事务方法外部
     * 3.不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制
     */
    public ShopExecution addShop(Shop shop, CommonsMultipartFile shopImg)
            throws RuntimeException {
        if (shop == null) {
            return new ShopExecution(ShopStateEnum.NULL_SHOP_INFO);
        }
        try {
            shop.setEnableStatus(0);
            shop.setCreateTime(new Date());
            shop.setLastEditTime(new Date());
            if (shop.getShopCategory() != null) {
                Long shopCategoryId = shop.getShopCategory()
                        .getShopCategoryId();
                ShopCategory sc = new ShopCategory();
                sc = shopCategoryMapper.queryShopCategoryById(shopCategoryId);
                ShopCategory parentCategory = new ShopCategory();
                parentCategory.setShopCategoryId(sc.getParentId());
                shop.setParentCategory(parentCategory);
            }
            int effectedNum = shopMapper.insertShop(shop);
            if (effectedNum <= 0) {
                throw new RuntimeException("店铺创建失败");
            } else {
                try {
                    if (shopImg != null) {
                        addShopImg(shop, shopImg);
                        effectedNum = shopMapper.updateShop(shop);
                        if (effectedNum <= 0) {
                            throw new RuntimeException("创建图片地址失败");
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("addShopImg error: "
                            + e.getMessage());
                }
                // 执行增加shopAuthMap操作
                ShopAuthMap shopAuthMap = new ShopAuthMap();
                shopAuthMap.setEmployeeId(shop.getOwnerId());
                shopAuthMap.setShopId(shop.getShopId());
                shopAuthMap.setName("");
                shopAuthMap.setTitle("Owner");
                shopAuthMap.setTitleFlag(1);
                shopAuthMap.setCreateTime(new Date());
                shopAuthMap.setLastEditTime(new Date());
                shopAuthMap.setEnableStatus(1);
                try {
                    effectedNum = shopAuthMapMapper.insertShopAuthMap(shopAuthMap);
                    if (effectedNum <= 0) {
                        throw new RuntimeException("授权创建失败");
                    } else {// 创建成功
                        return new ShopExecution(ShopStateEnum.CHECK, shop);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("insertShopAuthMap error: "
                            + e.getMessage());
                }

            }
        } catch (Exception e) {
            throw new RuntimeException("insertShop error: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ShopExecution modifyShop(Shop shop, CommonsMultipartFile shopImg)
            throws RuntimeException {
        if (shop == null || shop.getShopId() == null) {
            return new ShopExecution(ShopStateEnum.NULL_SHOPID);
        } else {
            try {
                if (shopImg != null) {
                    Shop tempShop = shopMapper.queryByShopId(shop.getShopId());
                    if (tempShop.getShopImg() != null) {
                        FileUtils.deleteFile(tempShop.getShopImg());
                    }
                    addShopImg(shop, shopImg);
                }
                shop.setLastEditTime(new Date());
                int effectedNum = shopMapper.updateShop(shop);
                if (effectedNum <= 0) {
                    return new ShopExecution(ShopStateEnum.INNER_ERROR);
                } else {// 创建成功
                    shop = shopMapper.queryByShopId(shop.getShopId());
                    return new ShopExecution(ShopStateEnum.SUCCESS, shop);
                }
            } catch (Exception e) {
                throw new RuntimeException("modifyShop error: "
                        + e.getMessage());
            }
        }
    }

    private void addShopImg(Shop shop, CommonsMultipartFile shopImg) {
        String dest = FileUtils.getShopImagePath(shop.getShopId());
        String shopImgAddr = ImageUtils.generateThumbnail(shopImg, dest);
        shop.setShopImg(shopImgAddr);
    }

}