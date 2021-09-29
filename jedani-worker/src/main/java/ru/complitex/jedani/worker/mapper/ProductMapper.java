package ru.complitex.jedani.worker.mapper;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.jedani.worker.entity.Product;

import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
public class ProductMapper extends BaseMapper {
    public List<Product> getProducts(FilterWrapper<Product> filterWrapper) {
        return sqlSession().selectList("selectProducts", filterWrapper);
    }

    public Long getProductsCount(FilterWrapper<Product> filterWrapper) {
        return sqlSession().selectOne("selectProductsCount", filterWrapper);
    }
}
