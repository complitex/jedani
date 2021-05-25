package ru.complitex.jedani.worker.mapper;

import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.jedani.worker.entity.Period;

import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 12.11.2019 3:51 PM
 */
public class PeriodMapper extends BaseMapper {
    public boolean hasPeriod(Period period){
        return sqlSession().selectOne("selectHasPeriod", period);
    }

    public Period getActualPeriod(){
        return sqlSession().selectOne("selectActualPeriod");
    }

    public List<Period> getPeriods(){
        return sqlSession().selectList("selectPeriods");
    }

    public Period getPeriod(Long periodId){
        return sqlSession().selectOne("selectPeriod", periodId);
    }

    public Period getPeriod(Date month){
        return sqlSession().selectOne("selectPeriodByMonth", month);
    }

    public Long getActualPeriodId(){
        return getActualPeriod().getObjectId();
    }

    public Date getActualOperatingMonth(){
        return getActualPeriod().getOperatingMonth();
    }
}
