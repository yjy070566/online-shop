package com.o2o.shop.service.impl;


import com.o2o.shop.bean.Product;
import com.o2o.shop.bean.ProductImg;
import com.o2o.shop.dto.ProductExecution;
import com.o2o.shop.enums.ProductStateEnum;
import com.o2o.shop.mapper.ProductImgMapper;
import com.o2o.shop.mapper.ProductMapper;
import com.o2o.shop.service.ProductService;
import com.o2o.shop.util.FileUtils;
import com.o2o.shop.util.ImageUtils;
import com.o2o.shop.util.PageCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductImgMapper productImgMapper;

    @Override
    public ProductExecution getProductList(Product productCondition, int pageIndex, int pageSize) {
        int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
        List<Product> productList = productMapper.queryProductList(productCondition, rowIndex, pageSize);
        int count = productMapper.queryProductCount(productCondition);
        ProductExecution pe = new ProductExecution();
        pe.setProductList(productList);
        pe.setCount(count);
        return pe;
    }

    @Override
    public Product getProductById(long productId) {
        return productMapper.queryProductByProductId(productId);
    }

    @Override
    @Transactional
    public ProductExecution addProduct(Product product, CommonsMultipartFile thumbnail,
                                       List<CommonsMultipartFile> productImgs) throws RuntimeException {
        if (product != null && product.getShop() != null && product.getShop().getShopId() != null) {
            product.setCreateTime(new Date());
            product.setLastEditTime(new Date());
            product.setEnableStatus(1);
            if (thumbnail != null) {
                addThumbnail(product, thumbnail);
            }
            try {
                int effectedNum = productMapper.insertProduct(product);
                if (effectedNum <= 0) {
                    throw new RuntimeException("创建商品失败");
                }
            } catch (Exception e) {
                throw new RuntimeException("创建商品失败:" + e.toString());
            }
            if (productImgs != null && productImgs.size() > 0) {
                addProductImgs(product, productImgs);
            }
            return new ProductExecution(ProductStateEnum.SUCCESS, product);
        } else {
            return new ProductExecution(ProductStateEnum.EMPTY);
        }
    }

    @Override
    @Transactional
    public ProductExecution modifyProduct(Product product, CommonsMultipartFile thumbnail,
                                          List<CommonsMultipartFile> productImgs) throws RuntimeException {
        if (product != null && product.getShop() != null && product.getShop().getShopId() != null) {
            product.setLastEditTime(new Date());
            if (thumbnail != null) {
                Product tempProduct = productMapper.queryProductByProductId(product.getProductId());
                if (tempProduct.getImgAddr() != null) {
                    FileUtils.deleteFile(tempProduct.getImgAddr());
                }
                addThumbnail(product, thumbnail);
            }
            if (productImgs != null && productImgs.size() > 0) {
                deleteProductImgs(product.getProductId());
                addProductImgs(product, productImgs);
            }
            try {
                int effectedNum = productMapper.updateProduct(product);
                if (effectedNum <= 0) {
                    throw new RuntimeException("更新商品信息失败");
                }
                return new ProductExecution(ProductStateEnum.SUCCESS, product);
            } catch (Exception e) {
                throw new RuntimeException("更新商品信息失败:" + e.toString());
            }
        } else {
            return new ProductExecution(ProductStateEnum.EMPTY);
        }
    }

    private void addProductImgs(Product product, List<CommonsMultipartFile> productImgs) {
        String dest = FileUtils.getShopImagePath(product.getShop().getShopId());
        List<String> imgAddrList = ImageUtils.generateNormalImgs(productImgs, dest);
        if (imgAddrList != null && imgAddrList.size() > 0) {
            List<ProductImg> productImgList = new ArrayList<ProductImg>();
            for (String imgAddr : imgAddrList) {
                ProductImg productImg = new ProductImg();
                productImg.setImgAddr(imgAddr);
                productImg.setProductId(product.getProductId());
                productImg.setCreateTime(new Date());
                productImgList.add(productImg);
            }
            try {
                int effectedNum = productImgMapper.batchInsertProductImg(productImgList);
                if (effectedNum <= 0) {
                    throw new RuntimeException("创建商品详情图片失败");
                }
            } catch (Exception e) {
                throw new RuntimeException("创建商品详情图片失败:" + e.toString());
            }
        }
    }

    private void deleteProductImgs(long productId) {
        List<ProductImg> productImgList = productImgMapper.queryProductImgList(productId);
        for (ProductImg productImg : productImgList) {
            FileUtils.deleteFile(productImg.getImgAddr());
        }
        productImgMapper.deleteProductImgByProductId(productId);
    }

    private void addThumbnail(Product product, CommonsMultipartFile thumbnail) {
        String dest = FileUtils.getShopImagePath(product.getShop().getShopId());
        String thumbnailAddr = ImageUtils.generateThumbnail(thumbnail, dest);
        product.setImgAddr(thumbnailAddr);
    }
}

