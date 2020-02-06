package com.o2o.shop.service.impl;

import com.o2o.shop.bean.ShopAuthMap;
import com.o2o.shop.dto.ShopAuthMapExecution;
import com.o2o.shop.enums.ShopAuthMapStateEnum;
import com.o2o.shop.mapper.ShopAuthMapMapper;
import com.o2o.shop.service.ShopAuthMapService;
import com.o2o.shop.util.PageCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ShopAuthMapServiceImpl implements ShopAuthMapService {
    @Autowired
    private ShopAuthMapMapper shopAuthMapMapper;

    @Override
    public ShopAuthMapExecution listShopAuthMapByShopId(Long shopId,
                                                        Integer pageIndex, Integer pageSize) {
        if (shopId != null && pageIndex != null && pageSize != null) {
            int beginIndex = PageCalculator.calculateRowIndex(pageIndex,
                    pageSize);
            List<ShopAuthMap> shopAuthMapList = shopAuthMapMapper
                    .queryShopAuthMapListByShopId(shopId, beginIndex, pageSize);
            int count = shopAuthMapMapper.queryShopAuthCountByShopId(shopId);
            ShopAuthMapExecution se = new ShopAuthMapExecution();
            se.setShopAuthMapList(shopAuthMapList);
            se.setCount(count);
            return se;
        } else {
            return null;
        }

    }

    @Override
    @Transactional
    public ShopAuthMapExecution addShopAuthMap(ShopAuthMap shopAuthMap)
            throws RuntimeException {
        if (shopAuthMap != null && shopAuthMap.getShopId() != null
                && shopAuthMap.getEmployeeId() != null) {
            shopAuthMap.setCreateTime(new Date());
            shopAuthMap.setLastEditTime(new Date());
            shopAuthMap.setEnableStatus(1);
            try {
                int effectedNum = shopAuthMapMapper.insertShopAuthMap(shopAuthMap);
                if (effectedNum <= 0) {
                    throw new RuntimeException("添加授权失败");
                }
                return new ShopAuthMapExecution(ShopAuthMapStateEnum.SUCCESS,
                        shopAuthMap);
            } catch (Exception e) {
                throw new RuntimeException("添加授权失败:" + e.toString());
            }
        } else {
            return new ShopAuthMapExecution(
                    ShopAuthMapStateEnum.NULL_SHOPAUTH_INFO);
        }
    }

    @Override
    @Transactional
    public ShopAuthMapExecution modifyShopAuthMap(ShopAuthMap shopAuthMap)
            throws RuntimeException {
        if (shopAuthMap == null || shopAuthMap.getShopAuthId() == null) {
            return new ShopAuthMapExecution(
                    ShopAuthMapStateEnum.NULL_SHOPAUTH_ID);
        } else {
            try {
                int effectedNum = shopAuthMapMapper.updateShopAuthMap(shopAuthMap);
                if (effectedNum <= 0) {
                    return new ShopAuthMapExecution(
                            ShopAuthMapStateEnum.INNER_ERROR);
                } else {// 创建成功
                    return new ShopAuthMapExecution(
                            ShopAuthMapStateEnum.SUCCESS, shopAuthMap);
                }
            } catch (Exception e) {
                throw new RuntimeException("updateShopByOwner error: "
                        + e.getMessage());
            }
        }
    }

    @Override
    public ShopAuthMapExecution removeShopAuthMap(Long shopAuthMapId)
            throws RuntimeException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ShopAuthMap getShopAuthMapById(Long shopAuthId) {
        return shopAuthMapMapper.queryShopAuthMapById(shopAuthId);
    }

}

