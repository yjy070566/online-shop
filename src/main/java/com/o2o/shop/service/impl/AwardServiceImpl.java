package com.o2o.shop.service.impl;



import com.o2o.shop.bean.Award;
import com.o2o.shop.dto.AwardExecution;
import com.o2o.shop.enums.AwardStateEnum;
import com.o2o.shop.mapper.AwardMapper;
import com.o2o.shop.service.AwardService;
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
public class AwardServiceImpl implements AwardService {

    @Autowired
    private AwardMapper awardMapper;

    @Override
    public AwardExecution getAwardList(Award awardCondition, int pageIndex,
                                       int pageSize) {
        int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
        List<Award> awardList = awardMapper.queryAwardList(awardCondition,
                rowIndex, pageSize);
        int count = awardMapper.queryAwardCount(awardCondition);
        AwardExecution ae = new AwardExecution();
        ae.setAwardList(awardList);
        ae.setCount(count);
        return ae;
    }

    @Override
    public Award getAwardById(long awardId) {
        return awardMapper.queryAwardByAwardId(awardId);
    }

    @Override
    @Transactional
    public AwardExecution addAward(Award award, CommonsMultipartFile thumbnail) {
        if (award != null && award.getShopId() != null) {
            award.setCreateTime(new Date());
            award.setLastEditTime(new Date());
            award.setEnableStatus(1);
            if (thumbnail != null) {
                addThumbnail(award, thumbnail);
            }
            try {
                int effectedNum = awardMapper.insertAward(award);
                if (effectedNum <= 0) {
                    throw new RuntimeException("创建商品失败");
                }
            } catch (Exception e) {
                throw new RuntimeException("创建商品失败:" + e.toString());
            }
            return new AwardExecution(AwardStateEnum.SUCCESS, award);
        } else {
            return new AwardExecution(AwardStateEnum.EMPTY);
        }
    }

    @Override
    @Transactional
    public AwardExecution modifyAward(Award award,
                                      CommonsMultipartFile thumbnail) {
        if (award != null && award.getShopId() != null) {
            award.setLastEditTime(new Date());
            if (thumbnail != null) {
                Award tempAward = awardMapper.queryAwardByAwardId(award
                        .getAwardId());
                if (tempAward.getAwardImg() != null) {
                    FileUtils.deleteFile(tempAward.getAwardImg());
                }
                addThumbnail(award, thumbnail);
            }
            try {
                int effectedNum = awardMapper.updateAward(award);
                if (effectedNum <= 0) {
                    throw new RuntimeException("更新商品信息失败");
                }
                return new AwardExecution(AwardStateEnum.SUCCESS, award);
            } catch (Exception e) {
                throw new RuntimeException("更新商品信息失败:" + e.toString());
            }
        } else {
            return new AwardExecution(AwardStateEnum.EMPTY);
        }
    }

    private void addThumbnail(Award award, CommonsMultipartFile thumbnail) {
        String dest = FileUtils.getShopImagePath(award.getShopId());
        String thumbnailAddr = ImageUtils.generateThumbnail(thumbnail, dest);
        award.setAwardImg(thumbnailAddr);
    }

}
