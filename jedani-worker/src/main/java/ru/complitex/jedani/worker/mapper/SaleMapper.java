package ru.complitex.jedani.worker.mapper;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.jedani.worker.entity.Sale;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 20.02.2019 20:32
 */
public class SaleMapper extends BaseMapper {
    public List<Sale> getSales(FilterWrapper<Sale> filterWrapper){
        return sqlSession().selectList("selectSales", filterWrapper);
    }

    public Long getSalesCount(FilterWrapper<Sale> filterWrapper){
        return sqlSession().selectOne("selectSalesCount", filterWrapper);
    }
}
