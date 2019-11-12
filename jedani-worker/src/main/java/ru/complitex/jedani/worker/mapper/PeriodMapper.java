package ru.complitex.jedani.worker.mapper;

import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.jedani.worker.entity.Period;

/**
 * @author Anatoly A. Ivanov
 * 12.11.2019 3:51 PM
 */
public class PeriodMapper extends BaseMapper {
    public boolean hasPeriod(Period period){
        return sqlSession().selectOne("selectHasPeriod", period);
    }
}
