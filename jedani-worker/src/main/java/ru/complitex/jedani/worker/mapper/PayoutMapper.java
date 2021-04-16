package ru.complitex.jedani.worker.mapper;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.jedani.worker.entity.Payout;

import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
public class PayoutMapper extends BaseMapper {
    public List<Payout> getPayouts(FilterWrapper<Payout> filterWrapper) {
        return sqlSession().selectList("selectPayouts", filterWrapper);
    }

    public Long getPayoutsCount(FilterWrapper<Payout> filterWrapper) {
        return sqlSession().selectOne("selectPayoutsCount", filterWrapper);
    }
}
