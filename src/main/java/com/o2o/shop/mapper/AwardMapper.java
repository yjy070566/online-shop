package com.o2o.shop.mapper;

import java.util.List;

import com.o2o.shop.bean.Award;
import org.apache.ibatis.annotations.Param;



public interface AwardMapper {
	List<Award> queryAwardList(@Param("awardCondition") Award awardCondition,
							   @Param("rowIndex") int rowIndex, @Param("pageSize") int pageSize);

	int queryAwardCount(@Param("awardCondition") Award awardCondition);

	Award queryAwardByAwardId(long awardId);

	int insertAward(Award award);

	int updateAward(Award award);

	int deleteAward(long awardId);
}
