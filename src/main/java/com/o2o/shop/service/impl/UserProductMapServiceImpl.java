package com.o2o.shop.service.impl;

import com.o2o.shop.bean.PersonInfo;
import com.o2o.shop.bean.Shop;
import com.o2o.shop.bean.UserProductMap;
import com.o2o.shop.bean.UserShopMap;
import com.o2o.shop.dto.UserProductMapExecution;
import com.o2o.shop.enums.UserProductMapStateEnum;
import com.o2o.shop.mapper.PersonInfoMapper;
import com.o2o.shop.mapper.ShopMapper;
import com.o2o.shop.mapper.UserProductMapMapper;
import com.o2o.shop.mapper.UserShopMapMapper;
import com.o2o.shop.service.UserProductMapService;
import com.o2o.shop.util.PageCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class UserProductMapServiceImpl implements UserProductMapService {
    @Autowired
    private UserProductMapMapper userProductMapMapper;
    @Autowired
    private UserShopMapMapper userShopMapMapper;
    @Autowired
    private PersonInfoMapper personInfoMapper;
    @Autowired
    private ShopMapper shopDao;

    @Override
    public UserProductMapExecution listUserProductMap(
            UserProductMap userProductCondition, Integer pageIndex,
            Integer pageSize) {
        if (userProductCondition != null && pageIndex != null
                && pageSize != null) {
            int beginIndex = PageCalculator.calculateRowIndex(pageIndex,
                    pageSize);
            List<UserProductMap> userProductMapList = userProductMapMapper
                    .queryUserProductMapList(userProductCondition, beginIndex,
                            pageSize);
            int count = userProductMapMapper
                    .queryUserProductMapCount(userProductCondition);
            UserProductMapExecution se = new UserProductMapExecution();
            se.setUserProductMapList(userProductMapList);
            se.setCount(count);
            return se;
        } else {
            return null;
        }

    }

    @Override
    @Transactional
    public UserProductMapExecution addUserProductMap(
            UserProductMap userProductMap) throws RuntimeException {
        if (userProductMap != null && userProductMap.getUserId() != null
                && userProductMap.getShopId() != null) {
            userProductMap.setCreateTime(new Date());
            try {
                int effectedNum = userProductMapMapper
                        .insertUserProductMap(userProductMap);
                if (effectedNum <= 0) {
                    throw new RuntimeException("添加消费记录失败");
                }
                if (userProductMap.getPoint() != null
                        && userProductMap.getPoint() > 0) {
                    UserShopMap userShopMap = userShopMapMapper.queryUserShopMap(
                            userProductMap.getUserId(),
                            userProductMap.getShopId());
                    if (userShopMap != null) {
                        if (userShopMap.getPoint() >= userProductMap.getPoint()) {
                            userShopMap.setPoint(userShopMap.getPoint()
                                    + userProductMap.getPoint());
                            effectedNum = userShopMapMapper
                                    .updateUserShopMapPoint(userShopMap);
                            if (effectedNum <= 0) {
                                throw new RuntimeException("更新积分信息失败");
                            }
                        }

                    } else {
                        // 在店铺没有过消费记录，添加一条积分信息
                        userShopMap = compactUserShopMap4Add(
                                userProductMap.getUserId(),
                                userProductMap.getShopId(),
                                userProductMap.getPoint());
                        effectedNum = userShopMapMapper
                                .insertUserShopMap(userShopMap);
                        if (effectedNum <= 0) {
                            throw new RuntimeException("积分信息创建失败");
                        }
                    }
                }
                return new UserProductMapExecution(
                        UserProductMapStateEnum.SUCCESS, userProductMap);
            } catch (Exception e) {
                throw new RuntimeException("添加授权失败:" + e.toString());
            }
        } else {
            return new UserProductMapExecution(
                    UserProductMapStateEnum.NULL_USERPRODUCT_INFO);
        }
    }

    private UserShopMap compactUserShopMap4Add(Long userId, Long shopId,
                                               Integer point) {
        UserShopMap userShopMap = null;
        if (userId != null && shopId != null) {
            userShopMap = new UserShopMap();
            PersonInfo personInfo = personInfoMapper.queryPersonInfoById(userId);
            Shop shop = shopDao.queryByShopId(shopId);
            userShopMap.setUserId(userId);
            userShopMap.setShopId(shopId);
            userShopMap.setUserName(personInfo.getName());
            userShopMap.setShopName(shop.getShopName());
            userShopMap.setCreateTime(new Date());
            userShopMap.setPoint(point);
        }
        return userShopMap;
    }

}
