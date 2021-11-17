package ru.complitex.jedani.worker.service.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.tuple.Pair;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.jedani.worker.entity.Payment;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.service.ExchangeRateService;
import ru.complitex.jedani.worker.service.PaymentService;
import ru.complitex.jedani.worker.service.SaleService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;

/**
 * @author Ivanov Anatoliy
 */
@ApplicationScoped
public class PaymentCacheService implements Serializable {
    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private PaymentService paymentService;

    @Inject
    private SaleService saleService;

    @Inject
    private ExchangeRateService exchangeRateService;

    private BigDecimal getPaymentRate(Long countryId, Long periodId) {
        Period period = periodMapper.getPeriod(periodId);

        Map<Long, BigDecimal> pointSumMap = new HashMap<>();
        Map<Long, BigDecimal> amountSumMap = new HashMap<>();

        paymentService.getPayments(FilterWrapper.of(new Payment().setPeriodId(period.getObjectId())))
                .forEach(p -> {
                    Long saleCountryId = saleService.getCountryId(p.getSaleId());

                    if (countryId.equals(saleCountryId)) {
                        pointSumMap.put(countryId, pointSumMap.getOrDefault(countryId, ZERO).add(p.getPoint()));
                        amountSumMap.put(countryId, amountSumMap.getOrDefault(countryId, ZERO).add(p.getAmount()));
                    }
                });

        List<BigDecimal> rates = new ArrayList<>();

        Date date = Dates.lastDayOfMonth(period.getOperatingMonth());

        amountSumMap.forEach((c, a) -> {
            if (a.compareTo(ZERO) != 0) {
                BigDecimal rate = a.divide(pointSumMap.get(c), 5, HALF_EVEN);

                if (!c.equals(countryId)) {
                    rate = rate
                            .multiply(exchangeRateService.getExchangeRate(countryId, date)
                                    .divide(exchangeRateService.getExchangeRate(c, date), 5, HALF_EVEN));
                }

                rates.add(rate);
            }
        });

        return rates.size() > 0 ? rates.stream().reduce(ZERO, BigDecimal::add).divide(new BigDecimal(rates.size()), 5, HALF_EVEN) : ONE;
    }

    private transient LoadingCache<Pair<Long, Long>, BigDecimal> paymentRateCache;

    private LoadingCache<Pair<Long, Long>, BigDecimal> getPaymentRateCache() {
        if (paymentRateCache == null ){
            paymentRateCache = CacheBuilder.newBuilder()
                    .build(CacheLoader.from(pair -> getPaymentRate(pair.getLeft(), pair.getRight())));
        }

        return paymentRateCache;
    }

    public BigDecimal getPaymentRateByCountryId(Long workerId, Long periodId) {
        try {
            return getPaymentRateCache().get(Pair.of(workerId, periodId));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        getPaymentRateCache().invalidateAll();
    }
}
