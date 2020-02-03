package com.o2o.shop.service;


import com.o2o.shop.bean.UserShopMap;
import com.o2o.shop.dto.UserShopMapExecution;

public interface UserShopMapService {

	UserShopMapExecution listUserShopMap(UserShopMap userShopMapCondition,
										 int pageIndex, int pageSize);

}
