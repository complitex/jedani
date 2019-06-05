package ru.complitex.jedani.worker.mapper;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.jedani.worker.entity.Price;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 16.04.2019 20:31
 */
public class PriceMapper extends BaseMapper {
    public List<Price> getPrices(FilterWrapper<Price> filterWrapper){
        return sqlSession().selectList("selectPrices", filterWrapper);
    }

    public Long getPricesCount(FilterWrapper<Price> filterWrapper){
        return sqlSession().selectOne("selectPricesCount", filterWrapper);
    }
}
