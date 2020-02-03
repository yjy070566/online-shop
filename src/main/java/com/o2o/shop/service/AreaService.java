package com.o2o.shop.service;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.o2o.shop.bean.Area;
import com.o2o.shop.dto.AreaExecution;


public interface AreaService {
	/**
	 * 
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	List<Area> getAreaList() throws JsonParseException, JsonMappingException,
			IOException;

	/**
	 * 
	 * @param area
	 * @return
	 */
	AreaExecution addArea(Area area);

	/**
	 * 
	 * @param area
	 * @return
	 */
	AreaExecution modifyArea(Area area);

	/**
	 * 
	 * @param areaId
	 * @return
	 */
	AreaExecution removeArea(long areaId);

	/**
	 * 
	 * @param areaIdList
	 * @return
	 */
	AreaExecution removeAreaList(List<Long> areaIdList);

}
