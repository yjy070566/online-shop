package com.o2o.shop.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.o2o.shop.bean.Area;
import com.o2o.shop.dto.AreaExecution;
import com.o2o.shop.mapper.AreaMapper;
import com.o2o.shop.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class AreaServiceImpl implements AreaService {
    @Autowired
    AreaMapper areaMapper;
    @Override
    public List<Area> getAreaList() throws JsonParseException, JsonMappingException, IOException {
        return null;
    }

    @Override
    public AreaExecution addArea(Area area) {
        return null;
    }

    @Override
    public AreaExecution modifyArea(Area area) {
        return null;
    }

    @Override
    public AreaExecution removeArea(long areaId) {
        return null;
    }

    @Override
    public AreaExecution removeAreaList(List<Long> areaIdList) {
        return null;
    }
}
