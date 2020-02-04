package com.o2o.shop.service.impl;


import ch.qos.logback.core.util.FileUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.o2o.shop.bean.ShopCategory;
import com.o2o.shop.cache.JedisUtil;
import com.o2o.shop.dto.ShopCategoryExecution;
import com.o2o.shop.enums.ShopCategoryStateEnum;
import com.o2o.shop.mapper.ShopCategoryMapper;
import com.o2o.shop.service.ShopCategoryService;
import com.o2o.shop.util.FileUtils;
import com.o2o.shop.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
@Service
public class ShopCategoryServiceImpl implements ShopCategoryService {


    @Autowired
    private ShopCategoryMapper shopCategoryMapper;

    private static String SCLISTKEY = "shopcategorylist";

    @Override
    public List<ShopCategory> getFirstLevelShopCategoryList()
            throws IOException {
        String key = SCLISTKEY;
        List<ShopCategory> shopCategoryList = null;
        ObjectMapper mapper = new ObjectMapper();

            ShopCategory shopCategoryCondition = new ShopCategory();
            // 当shopCategoryId不为空的时候，查询的条件会变为 where parent_id is null
            shopCategoryCondition.setShopCategoryId(-1L);
            shopCategoryList = shopCategoryMapper
                    .queryShopCategory(shopCategoryCondition);
            String jsonString = mapper.writeValueAsString(shopCategoryList);


        return shopCategoryList;
    }

    @Override
    public List<ShopCategory> getShopCategoryList(Long parentId)
            throws IOException {
        String key = SCLISTKEY + "_" + parentId;
        List<ShopCategory> shopCategoryList = null;
        ObjectMapper mapper = new ObjectMapper();

            ShopCategory shopCategoryCondition = new ShopCategory();
            shopCategoryCondition.setParentId(parentId);
            shopCategoryList = shopCategoryMapper
                    .queryShopCategory(shopCategoryCondition);
            String jsonString = mapper.writeValueAsString(shopCategoryList);


        return shopCategoryList;
    }

    @Override
    public List<ShopCategory> getAllSecondLevelShopCategory()
            throws IOException {
        String key = SCLISTKEY + "ALLSECOND";
        List<ShopCategory> shopCategoryList = null;
        ObjectMapper mapper = new ObjectMapper();

            ShopCategory shopCategoryCondition = new ShopCategory();
            // 当shopCategoryDesc不为空的时候，查询的条件会变为 where parent_id is not null
            shopCategoryCondition.setShopCategoryDesc("ALLSECOND");
            shopCategoryList = shopCategoryMapper.queryShopCategory(shopCategoryCondition);
            String jsonString = mapper.writeValueAsString(shopCategoryList);

        return shopCategoryList;
    }

    @Override
    public ShopCategory getShopCategoryById(Long shopCategoryId) {
        List<ShopCategory> shopCategoryList = new ArrayList<ShopCategory>();
        try {
            shopCategoryList = getFirstLevelShopCategoryList();
            shopCategoryList.addAll(getAllSecondLevelShopCategory());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (ShopCategory sc : shopCategoryList) {
            if (shopCategoryId == sc.getShopCategoryId()) {
                return sc;
            }
        }
        ShopCategory sc = shopCategoryMapper.queryShopCategoryById(shopCategoryId);
        if (sc != null) {
            return sc;
        } else {
            return null;
        }

    }

    @Override
    @Transactional
    public ShopCategoryExecution addShopCategory(ShopCategory shopCategory,
                                                 CommonsMultipartFile thumbnail) {
        if (shopCategory != null) {
            shopCategory.setCreateTime(new Date());
            shopCategory.setLastEditTime(new Date());
            if (thumbnail != null) {
                addThumbnail(shopCategory, thumbnail);
            }
            try {
                int effectedNum = shopCategoryMapper
                        .insertShopCategory(shopCategory);
                if (effectedNum > 0) {
                    String prefix = SCLISTKEY;

                    return new ShopCategoryExecution(
                            ShopCategoryStateEnum.SUCCESS, shopCategory);
                } else {
                    return new ShopCategoryExecution(
                            ShopCategoryStateEnum.INNER_ERROR);
                }
            } catch (Exception e) {
                throw new RuntimeException("添加店铺类别信息失败:" + e.toString());
            }
        } else {
            return new ShopCategoryExecution(ShopCategoryStateEnum.EMPTY);
        }
    }

    @Override
    @Transactional
    public ShopCategoryExecution modifyShopCategory(ShopCategory shopCategory,
                                                    CommonsMultipartFile thumbnail, boolean thumbnailChange) {
        if (shopCategory.getShopCategoryId() != null
                && shopCategory.getShopCategoryId() > 0) {
            shopCategory.setLastEditTime(new Date());
            if (thumbnail != null && thumbnailChange == true) {
                ShopCategory tempShopCategory = shopCategoryMapper
                        .queryShopCategoryById(shopCategory.getShopCategoryId());
                if (tempShopCategory.getShopCategoryImg() != null) {
                    FileUtils.deleteFile(tempShopCategory.getShopCategoryImg());
                }
                addThumbnail(shopCategory, thumbnail);
            }
            try {
                int effectedNum = shopCategoryMapper
                        .updateShopCategory(shopCategory);
                if (effectedNum > 0) {
                    String prefix = SCLISTKEY;

                    return new ShopCategoryExecution(
                            ShopCategoryStateEnum.SUCCESS, shopCategory);
                } else {
                    return new ShopCategoryExecution(
                            ShopCategoryStateEnum.INNER_ERROR);
                }
            } catch (Exception e) {
                throw new RuntimeException("更新店铺类别信息失败:" + e.toString());
            }
        } else {
            return new ShopCategoryExecution(ShopCategoryStateEnum.EMPTY);
        }
    }

    @Override
    @Transactional
    public ShopCategoryExecution removeShopCategory(long shopCategoryId) {
        if (shopCategoryId > 0) {
            try {
                ShopCategory tempShopCategory = shopCategoryMapper
                        .queryShopCategoryById(shopCategoryId);
                if (tempShopCategory.getShopCategoryImg() != null) {
                    FileUtils.deleteFile(tempShopCategory.getShopCategoryImg());
                }
                int effectedNum = shopCategoryMapper
                        .deleteShopCategory(shopCategoryId);
                if (effectedNum > 0) {
                    String prefix = SCLISTKEY;
                    return new ShopCategoryExecution(
                            ShopCategoryStateEnum.SUCCESS);
                } else {
                    return new ShopCategoryExecution(
                            ShopCategoryStateEnum.INNER_ERROR);
                }
            } catch (Exception e) {
                throw new RuntimeException("删除店铺类别信息失败:" + e.toString());
            }
        } else {
            return new ShopCategoryExecution(ShopCategoryStateEnum.EMPTY);
        }
    }

    @Override
    @Transactional
    public ShopCategoryExecution removeShopCategoryList(
            List<Long> shopCategoryIdList) {
        if (shopCategoryIdList != null && shopCategoryIdList.size() > 0) {
            try {
                List<ShopCategory> shopCategoryList = shopCategoryMapper
                        .queryShopCategoryByIds(shopCategoryIdList);
                for (ShopCategory shopCategory : shopCategoryList) {
                    if (shopCategory.getShopCategoryImg() != null) {
                        FileUtils.deleteFile(shopCategory.getShopCategoryImg());
                    }
                }
                int effectedNum = shopCategoryMapper
                        .batchDeleteShopCategory(shopCategoryIdList);
                if (effectedNum > 0) {
                    String prefix = SCLISTKEY;

                    return new ShopCategoryExecution(
                            ShopCategoryStateEnum.SUCCESS);
                } else {
                    return new ShopCategoryExecution(
                            ShopCategoryStateEnum.INNER_ERROR);
                }
            } catch (Exception e) {
                throw new RuntimeException("删除店铺类别信息失败:" + e.toString());
            }
        } else {
            return new ShopCategoryExecution(ShopCategoryStateEnum.EMPTY);
        }
    }

    private void addThumbnail(ShopCategory shopCategory,
                              CommonsMultipartFile thumbnail) {
        String dest = FileUtils.getShopCategoryImagePath();
        String thumbnailAddr = ImageUtils.generateNormalImg(thumbnail, dest);
        shopCategory.setShopCategoryImg(thumbnailAddr);
    }
};

