package ru.complitex.jedani.worker.service;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Currency;
import ru.complitex.jedani.worker.entity.Payment;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.mapper.PaymentMapper;

import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author Anatoly A. Ivanov
 * 28.10.2019 6:57 PM
 */
public class PaymentService implements Serializable {
    @Inject
    private PaymentMapper paymentMapper;

    public List<Payment> getPaymentsBySaleId(Long saleId){
        return paymentMapper.getPayments(FilterWrapper.of(new Payment().setSaleId(saleId)));
    }

    public BigDecimal getPaymentsVolumeBySaleId(Long saleId){
        return getPaymentsBySaleId(saleId).stream()
                .reduce(BigDecimal.ZERO, ((t, p) -> t.add(p.getPoint())), BigDecimal::add);
    }

    public BigDecimal getPaymentsVolumeBySaleId(Long saleId, Date month){
        return getPaymentsBySaleId(saleId).stream()
                .filter(p -> p.getDate().before(Dates.lastDayOfMonth(month)))
                .reduce(BigDecimal.ZERO, ((t, p) -> t.add(p.getPoint())), BigDecimal::add);
    }

    public List<Payment> getPaymentsBySellerWorkerId(Long sellerWorkerId){
        return paymentMapper.getPayments(FilterWrapper.of(new Payment()).put(Payment.FILTER_SELLER_WORKER_ID, sellerWorkerId));
    }

    public BigDecimal getPaymentsVolumeBySellerWorkerId(Long sellerWorkerId){
        return getPaymentsBySellerWorkerId(sellerWorkerId).stream()
                .reduce(BigDecimal.ZERO, ((t, p) -> t.add(p.getPoint())), BigDecimal::add);
    }

    public BigDecimal getPaymentsVolumeBySellerWorkerId(Long sellerWorkerId, Period period){
        return getPaymentsBySellerWorkerId(sellerWorkerId).stream()
                .filter(p -> Objects.equals(p.getPeriodId(), period.getObjectId()))
                .reduce(BigDecimal.ZERO, ((t, p) -> t.add(p.getPoint())), BigDecimal::add);
    }

    public BigDecimal getYearPaymentsVolumeBySellerWorkerId(Long sellerWorkerId){
        Date date = Dates.previousYear();

        return getPaymentsBySellerWorkerId(sellerWorkerId).stream()
                .filter(p -> p.getDate().after(date))
                .reduce(BigDecimal.ZERO, ((t, p) -> t.add(p.getPoint())), BigDecimal::add);
    }

    public List<Payment> getPayments(FilterWrapper<Payment> filterWrapper){
        return paymentMapper.getPayments(filterWrapper);
    }
}
