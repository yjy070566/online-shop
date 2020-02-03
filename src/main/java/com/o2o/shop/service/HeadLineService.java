package com.o2o.shop.service;

import java.io.IOException;
import java.util.List;

import com.o2o.shop.bean.HeadLine;
import com.o2o.shop.dto.HeadLineExecution;
import org.springframework.web.multipart.commons.CommonsMultipartFile;


public interface HeadLineService {

	/**
	 * 
	 * @param headLineCondition
	 * @return
	 * @throws IOException
	 */
	List<HeadLine> getHeadLineList(HeadLine headLineCondition)
			throws IOException;

	/**
	 * 
	 * @param headLine
	 * @param thumbnail
	 * @return
	 */
	HeadLineExecution addHeadLine(HeadLine headLine,
								  CommonsMultipartFile thumbnail);

	/**
	 * 
	 * @param headLine
	 * @param thumbnail
	 * @param thumbnailChange
	 * @return
	 */
	HeadLineExecution modifyHeadLine(HeadLine headLine,
                                     CommonsMultipartFile thumbnail);

	/**
	 * 
	 * @param headLineId
	 * @return
	 */
	HeadLineExecution removeHeadLine(long headLineId);

	/**
	 * 
	 * @param headLineIdList
	 * @return
	 */
	HeadLineExecution removeHeadLineList(List<Long> headLineIdList);

}
