package ru.complitex.jedani.worker.mapper;

import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.common.util.Maps;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Ivanov Anatoliy
 */
public class RewardParameterMapper extends BaseMapper {
    public BigDecimal getRewardParameterValue(Long parameterId, Date date) {
        String value =  sqlSession().selectOne("selectRewardParameterValue", Maps.of("parameterId", parameterId, "date", date));

        return value != null ? new BigDecimal(value) : BigDecimal.ZERO;
    }
}
