package ru.complitex.jedani.worker.mapper;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.jedani.worker.entity.Transfer;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 05.12.2018 18:38
 */
public class TransferMapper extends BaseMapper {
    public List<Transfer> getTransfers(FilterWrapper<Transfer> filterWrapper){
        return sqlSession().selectList("selectTransfers", filterWrapper);
    }

    public Long getTransfersCount(FilterWrapper<Transfer> filterWrapper){
        return sqlSession().selectOne("selectTransfersCount", filterWrapper);
    }
}
