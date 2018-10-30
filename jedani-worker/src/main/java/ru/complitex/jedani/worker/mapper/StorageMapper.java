package ru.complitex.jedani.worker.mapper;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.jedani.worker.entity.Storage;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 30.10.2018 19:16
 */
public class StorageMapper extends BaseMapper {
    public List<Storage> getStorages(FilterWrapper<Storage> filterWrapper){
        return sqlSession().selectList("selectStorages", filterWrapper);
    }

    public Long getStoragesCount(FilterWrapper<Storage> filterWrapper){
        return sqlSession().selectOne("selectStoragesCount", filterWrapper);
    }
}
