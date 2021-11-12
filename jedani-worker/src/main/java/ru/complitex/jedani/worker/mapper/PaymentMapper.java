package ru.complitex.jedani.worker.mapper;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.jedani.worker.entity.Payment;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 28.10.2019 9:18 PM
 */
public class PaymentMapper extends BaseMapper {
    public List<Payment> getPayments(FilterWrapper<Payment> filterWrapper){
        return sqlSession().selectList("selectPayments", filterWrapper);
    }

    public Long getPaymentsCount(FilterWrapper<Payment> filterWrapper){
        return sqlSession().selectOne("selectPaymentsCount", filterWrapper);
    }

    public BigDecimal getPaymentsPointSum(Long saleId) {
        return sqlSession().selectOne("selectPaymentsPointSum", saleId);
    }
}
