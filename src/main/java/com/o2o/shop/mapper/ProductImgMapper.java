package com.o2o.shop.mapper;

import com.o2o.shop.bean.ProductImg;

import java.util.List;



public interface ProductImgMapper {

	List<ProductImg> queryProductImgList(long productId);

	int batchInsertProductImg(List<ProductImg> productImgList);

	int deleteProductImgByProductId(long productId);
}
