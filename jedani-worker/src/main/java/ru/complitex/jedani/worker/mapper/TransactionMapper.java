package ru.complitex.jedani.worker.mapper;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.jedani.worker.entity.Transaction;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 05.12.2018 18:38
 */
public class TransactionMapper extends BaseMapper {
    public List<Transaction> getTransactions(FilterWrapper<Transaction> filterWrapper){
        return sqlSession().selectList("selectTransactions", filterWrapper);
    }

    public Long getTransactionsCount(FilterWrapper<Transaction> filterWrapper){
        return sqlSession().selectOne("selectTransactionsCount", filterWrapper);
    }
}
