package ru.complitex.jedani.worker.service;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Payment;

import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 28.10.2019 6:57 PM
 */
public class PaymentService implements Serializable {
    @Inject
    private DomainService domainService;

    public List<Payment> getPaymentsBySaleId(Long saleId){
        return domainService.getDomains(Payment.class, FilterWrapper.of(new Payment().setSaleId(saleId)));
    }

    public BigDecimal getPaymentsTotalBySaleId(Long saleId){
        return getPaymentsBySaleId(saleId).stream()
                .reduce(BigDecimal.ZERO, ((t, p) -> t.add(p.getPoint())), BigDecimal::add);
    }

    public List<Payment> getPaymentsBySellerWorkerId
}
