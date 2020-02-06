package com.o2o.shop.service.impl;

import com.o2o.shop.bean.PersonInfo;
import com.o2o.shop.bean.WechatAuth;
import com.o2o.shop.dto.WechatAuthExecution;
import com.o2o.shop.enums.WechatAuthStateEnum;
import com.o2o.shop.mapper.PersonInfoMapper;
import com.o2o.shop.mapper.WechatAuthMapper;
import com.o2o.shop.service.WechatAuthService;
import com.o2o.shop.util.FileUtils;
import com.o2o.shop.util.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.Date;

@Service
public class WechatAuthServiceImpl implements WechatAuthService {
    private static Logger log = LoggerFactory
            .getLogger(WechatAuthServiceImpl.class);
    @Autowired
    private WechatAuthMapper wechatAuthMapper;
    @Autowired
    private PersonInfoMapper personInfoMapper;

    @Override
    public WechatAuth getWechatAuthByOpenId(String openId) {
        return wechatAuthMapper.queryWechatInfoByOpenId(openId);
    }

    @Override
    @Transactional
    public WechatAuthExecution register(WechatAuth wechatAuth,
                                        CommonsMultipartFile profileImg) throws RuntimeException {
        if (wechatAuth == null || wechatAuth.getOpenId() == null) {
            return new WechatAuthExecution(WechatAuthStateEnum.NULL_AUTH_INFO);
        }
        try {
            wechatAuth.setCreateTime(new Date());
            if (wechatAuth.getPersonInfo() != null
                    && wechatAuth.getPersonInfo().getUserId() == null) {
                if (profileImg != null) {
                    try {
                        addProfileImg(wechatAuth, profileImg);
                    } catch (Exception e) {
                        log.debug("addUserProfileImg error:" + e.toString());
                        throw new RuntimeException("addUserProfileImg error: "
                                + e.getMessage());
                    }
                }
                try {
                    wechatAuth.getPersonInfo().setCreateTime(new Date());
                    wechatAuth.getPersonInfo().setLastEditTime(new Date());
                    wechatAuth.getPersonInfo().setCustomerFlag(1);
                    wechatAuth.getPersonInfo().setShopOwnerFlag(1);
                    wechatAuth.getPersonInfo().setAdminFlag(0);
                    wechatAuth.getPersonInfo().setEnableStatus(1);
                    PersonInfo personInfo = wechatAuth.getPersonInfo();
                    int effectedNum = personInfoMapper
                            .insertPersonInfo(personInfo);
                    wechatAuth.setUserId(personInfo.getUserId());
                    if (effectedNum <= 0) {
                        throw new RuntimeException("添加用户信息失败");
                    }
                } catch (Exception e) {
                    log.debug("insertPersonInfo error:" + e.toString());
                    throw new RuntimeException("insertPersonInfo error: "
                            + e.getMessage());
                }
            }
            int effectedNum = wechatAuthMapper.insertWechatAuth(wechatAuth);
            if (effectedNum <= 0) {
                throw new RuntimeException("帐号创建失败");
            } else {
                return new WechatAuthExecution(WechatAuthStateEnum.SUCCESS,
                        wechatAuth);
            }
        } catch (Exception e) {
            log.debug("insertWechatAuth error:" + e.toString());
            throw new RuntimeException("insertWechatAuth error: "
                    + e.getMessage());
        }
    }

    private void addProfileImg(WechatAuth wechatAuth,
                               CommonsMultipartFile profileImg) {
        String dest = FileUtils.getPersonInfoImagePath();
        String profileImgAddr = ImageUtils.generateThumbnail(profileImg, dest);
        wechatAuth.getPersonInfo().setProfileImg(profileImgAddr);
    }

}
