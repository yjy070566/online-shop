package com.o2o.shop.service;

import com.o2o.shop.bean.WechatAuth;
import com.o2o.shop.dto.WechatAuthExecution;
import org.springframework.web.multipart.commons.CommonsMultipartFile;



public interface WechatAuthService {

	/**
	 * 
	 * @param openId
	 * @return
	 */
	WechatAuth getWechatAuthByOpenId(String openId);

	/**
	 * 
	 * @param wechatAuth
	 * @param profileImg
	 * @return
	 * @throws RuntimeException
	 */
	WechatAuthExecution register(WechatAuth wechatAuth,
								 CommonsMultipartFile profileImg) throws RuntimeException;

}
