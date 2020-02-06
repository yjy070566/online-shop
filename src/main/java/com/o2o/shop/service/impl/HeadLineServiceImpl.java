package com.o2o.shop.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.o2o.shop.bean.HeadLine;
import com.o2o.shop.dto.HeadLineExecution;
import com.o2o.shop.mapper.HeadLineMapper;
import com.o2o.shop.service.HeadLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class HeadLineServiceImpl implements HeadLineService {
    @Autowired
    HeadLineMapper headLineMapper;

    @Override
    public List<HeadLine> getHeadLineList(HeadLine headLineCondition) throws IOException {

        ObjectMapper mapper =new ObjectMapper();
        List<HeadLine> listHeadLine =null;
        listHeadLine=headLineMapper.queryHeadLine(headLineCondition);


        return null;
    }

    @Override
    public HeadLineExecution addHeadLine(HeadLine headLine, CommonsMultipartFile thumbnail) {
        return null;
    }

    @Override
    public HeadLineExecution modifyHeadLine(HeadLine headLine, CommonsMultipartFile thumbnail) {
        return null;
    }

    @Override
    public HeadLineExecution removeHeadLine(long headLineId) {
        return null;
    }

    @Override
    public HeadLineExecution removeHeadLineList(List<Long> headLineIdList) {
        return null;
    }
}
