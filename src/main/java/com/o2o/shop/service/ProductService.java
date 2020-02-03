package com.o2o.shop.service;

import java.util.List;

import com.o2o.shop.bean.Product;
import com.o2o.shop.dto.ProductExecution;
import org.springframework.web.multipart.commons.CommonsMultipartFile;



public interface ProductService {
	ProductExecution getProductList(Product productCondition, int pageIndex, int pageSize);

	Product getProductById(long productId);

	ProductExecution addProduct(Product product, CommonsMultipartFile thumbnail, List<CommonsMultipartFile> productImgs)
			throws RuntimeException;

	ProductExecution modifyProduct(Product product, CommonsMultipartFile thumbnail,
                                   List<CommonsMultipartFile> productImgs) throws RuntimeException;
}
