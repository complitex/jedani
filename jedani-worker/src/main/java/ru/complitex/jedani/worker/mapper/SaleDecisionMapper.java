package ru.complitex.jedani.worker.mapper;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.jedani.worker.entity.SaleDecision;

import java.util.List;

public class SaleDecisionMapper extends BaseMapper {
    public List<SaleDecision> getSaleDecisions(FilterWrapper<SaleDecision> filterWrapper){
        return sqlSession().selectList("selectSaleDecisions", filterWrapper);
    }

    public Long getSaleDecisionsCount(FilterWrapper<SaleDecision> filterWrapper){
        return sqlSession().selectOne("selectSaleDecisionsCount", filterWrapper);
    }
}
