package ru.complitex.jedani.worker.mapper;

import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.jedani.worker.entity.Product;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 23.10.2018 16:15
 */
public class ProductMapper extends BaseMapper {
    public List<Product> getProducts(String s){
        return sqlSession().selectList("selectProductsByString", s);
    }
}
