package ru.complitex.jedani.worker.mapper;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.jedani.worker.entity.SaleItem;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 20.02.2019 20:32
 */
public class SaleItemMapper extends BaseMapper {
    public List<SaleItem> getSaleItems(FilterWrapper<SaleItem> filterWrapper){
        return sqlSession().selectList("selectSaleItems", filterWrapper);
    }

    public Long getSaleItemsCount(FilterWrapper<SaleItem> filterWrapper){
        return sqlSession().selectOne("selectSaleItemsCount", filterWrapper);
    }
}
