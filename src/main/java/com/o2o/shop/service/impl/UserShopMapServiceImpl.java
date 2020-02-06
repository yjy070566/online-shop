package com.o2o.shop.service.impl;

import com.o2o.shop.bean.UserShopMap;
import com.o2o.shop.dto.UserShopMapExecution;
import com.o2o.shop.mapper.UserShopMapMapper;
import com.o2o.shop.service.UserShopMapService;
import com.o2o.shop.util.PageCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserShopMapServiceImpl implements UserShopMapService {
    @Autowired
    private UserShopMapMapper userShopMapMapper;

    @Override
    public UserShopMapExecution listUserShopMap(
            UserShopMap userShopMapCondition, int pageIndex, int pageSize) {
        if (userShopMapCondition != null && pageIndex != -1 && pageSize != -1) {
            int beginIndex = PageCalculator.calculateRowIndex(pageIndex,
                    pageSize);
            List<UserShopMap> userShopMapList = userShopMapMapper
                    .queryUserShopMapList(userShopMapCondition, beginIndex,
                            pageSize);
            int count = userShopMapMapper
                    .queryUserShopMapCount(userShopMapCondition);
            UserShopMapExecution ue = new UserShopMapExecution();
            ue.setUserShopMapList(userShopMapList);
            ue.setCount(count);
            return ue;
        } else {
            return null;
        }

    }
}
