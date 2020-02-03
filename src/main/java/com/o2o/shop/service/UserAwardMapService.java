package com.o2o.shop.service;


import com.o2o.shop.bean.UserAwardMap;
import com.o2o.shop.dto.UserAwardMapExecution;

public interface UserAwardMapService {

	/**
	 * 
	 * @param userAwardCondition
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	UserAwardMapExecution listUserAwardMap(UserAwardMap userAwardCondition,
										   Integer pageIndex, Integer pageSize);

	/**
	 * 
	 * @param userAwardMapId
	 * @return
	 */
	UserAwardMap getUserAwardMapById(long userAwardMapId);

	/**
	 * 
	 * @param userAwardMap
	 * @return
	 * @throws RuntimeException
	 */
	UserAwardMapExecution addUserAwardMap(UserAwardMap userAwardMap)
			throws RuntimeException;

	/**
	 * 
	 * @param userAwardMap
	 * @return
	 * @throws RuntimeException
	 */
	UserAwardMapExecution modifyUserAwardMap(UserAwardMap userAwardMap)
			throws RuntimeException;

}
