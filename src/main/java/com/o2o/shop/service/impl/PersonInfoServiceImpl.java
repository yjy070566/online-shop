package com.o2o.shop.service.impl;

import com.o2o.shop.bean.PersonInfo;
import com.o2o.shop.dto.PersonInfoExecution;
import com.o2o.shop.enums.PersonInfoStateEnum;
import com.o2o.shop.mapper.PersonInfoMapper;
import com.o2o.shop.service.PersonInfoService;
import com.o2o.shop.util.PageCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonInfoServiceImpl implements PersonInfoService {
    @Autowired
    private PersonInfoMapper personInfoMapper;

    @Override
    public PersonInfo getPersonInfoById(Long userId) {
        return personInfoMapper.queryPersonInfoById(userId);
    }

    @Override
    public PersonInfoExecution getPersonInfoList(
            PersonInfo personInfoCondition, int pageIndex, int pageSize) {
        int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
        List<PersonInfo> personInfoList = personInfoMapper.queryPersonInfoList(
                personInfoCondition, rowIndex, pageSize);
        int count = personInfoMapper.queryPersonInfoCount(personInfoCondition);
        PersonInfoExecution se = new PersonInfoExecution();
        if (personInfoList != null) {
            se.setPersonInfoList(personInfoList);
            se.setCount(count);
        } else {
            se.setState(PersonInfoStateEnum.INNER_ERROR.getState());
        }
        return se;
    }

    @Override
    @Transactional
    public PersonInfoExecution addPersonInfo(PersonInfo personInfo) {
        if (personInfo == null) {
            return new PersonInfoExecution(PersonInfoStateEnum.EMPTY);
        } else {
            try {
                int effectedNum = personInfoMapper.insertPersonInfo(personInfo);
                if (effectedNum <= 0) {
                    return new PersonInfoExecution(
                            PersonInfoStateEnum.INNER_ERROR);
                } else {// 创建成功
                    personInfo = personInfoMapper.queryPersonInfoById(personInfo
                            .getUserId());
                    return new PersonInfoExecution(PersonInfoStateEnum.SUCCESS,
                            personInfo);
                }
            } catch (Exception e) {
                throw new RuntimeException("addPersonInfo error: "
                        + e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public PersonInfoExecution modifyPersonInfo(PersonInfo personInfo) {
        if (personInfo == null || personInfo.getUserId() == null) {
            return new PersonInfoExecution(PersonInfoStateEnum.EMPTY);
        } else {
            try {
                int effectedNum = personInfoMapper.updatePersonInfo(personInfo);
                if (effectedNum <= 0) {
                    return new PersonInfoExecution(
                            PersonInfoStateEnum.INNER_ERROR);
                } else {// 创建成功
                    personInfo = personInfoMapper.queryPersonInfoById(personInfo
                            .getUserId());
                    return new PersonInfoExecution(PersonInfoStateEnum.SUCCESS,
                            personInfo);
                }
            } catch (Exception e) {
                throw new RuntimeException("updatePersonInfo error: "
                        + e.getMessage());
            }
        }
    }

}
