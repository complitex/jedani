package ru.complitex.jedani.worker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.mapper.PriceMapper;

import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

public class PriceService implements Serializable {
    private final static Logger log = LoggerFactory.getLogger(Price.class);

    @Inject
    private PriceMapper priceMapper;

    @Inject
    private SaleDecisionService saleDecisionService;

    @Inject
    private ExchangeRateService exchangeRateService;

    @Inject
    private StorageService storageService;

    public BigDecimal getBasePrice(Long storageId, Long nomenclatureId, Date date){
        if (storageId == null || nomenclatureId == null || date == null){
            return null;
        }

        List<Price> prices = priceMapper.getPrices(FilterWrapper.of(new Price()
                .setCountryId(storageService.getCountryId(storageId))
                .setNomenclatureId(nomenclatureId))
                .put(Price.FILTER_DATE, date));

        if (!prices.isEmpty()){
            if (prices.size() > 1){
                log.warn("More than one price for nomenclature {}", prices);
            }

            return prices.get(0).getPrice();
        }

        return null;
    }

    public SaleDecision getSaleDecision(Long storageId, Long nomenclatureId, Date date, BigDecimal total,
                                        Long installmentMonths, boolean yourself){
        if (storageId == null || nomenclatureId == null || date == null || total == null){
            return null;
        }

        List<SaleDecision> saleDecisions = saleDecisionService.getSaleDecisions(storageService.getCountryId(storageId),
                nomenclatureId, date);

        for (SaleDecision saleDecision : saleDecisions){
            saleDecisionService.loadRules(saleDecision);

            for (Rule rule : saleDecision.getRules()){
                if (saleDecisionService.check(rule, date, total, installmentMonths, yourself)){
                    return saleDecision;
                }
            }
        }

        return null;
    }

    public BigDecimal getPrice(SaleDecision saleDecision, Date date, BigDecimal basePrice, BigDecimal total,
                               Long installmentMonths, boolean yourself){
        if (saleDecision == null || date == null || basePrice == null || total == null){
            return basePrice;
        }

        for (Rule rule : saleDecision.getRules()){
            if (saleDecisionService.check(rule, date, total, installmentMonths, yourself)){
                for (RuleAction a : rule.getActions()){
                    switch (RuleActionType.getValue(a.getType())){
                        case DISCOUNT:
                            BigDecimal discount = a.getDecimal(RuleAction.ACTION);

                            BigDecimal BD_100 = new BigDecimal(100);

                            if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0 &&
                                    discount.compareTo(BD_100) <= 0){
                                return basePrice.multiply(BD_100.subtract(discount)
                                        .divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN));
                            }

                            break;
                        case PRICE:
                            return a.getDecimal(RuleAction.ACTION);
                    }
                }
            }
        }

        return basePrice;
    }

    public BigDecimal getRate(Long storageId, Long nomenclatureId, Date date){
        if (storageId == null || nomenclatureId == null || date == null){
            return null;
        }

        return exchangeRateService.getExchangeRateValue(storageService.getCountryId(storageId), date);
    }

    public BigDecimal getRate(SaleDecision saleDecision, Date paymentDate, BigDecimal rate, BigDecimal total,
                              Long installmentMonths, boolean yourself){
        if (saleDecision == null || paymentDate == null || rate == null || total == null){
            return rate;
        }

        for (Rule rule : saleDecision.getRules()){
            if (saleDecisionService.check(rule, paymentDate, total, installmentMonths, yourself)){
                for (RuleAction a : rule.getActions()){
                    if (RuleActionType.getValue(a.getType()) == RuleActionType.EURO_RATE_LESS_OR_EQUAL) {
                        BigDecimal actionRate = a.getDecimal(RuleAction.ACTION);

                        return actionRate != null ? rate.min(actionRate) : rate;
                    }
                }
            }
        }

        return rate;
    }

    public BigDecimal getRate(Long storageId, Long nomenclatureId, SaleDecision saleDecision, Date paymentDate,
                              BigDecimal total, Long installmentMonths, boolean yourself){
        BigDecimal rate = getRate(storageId, nomenclatureId, paymentDate);

        if (saleDecision != null){
            return getRate(saleDecision, paymentDate, rate, total, installmentMonths, yourself);
        }

        return rate;
    }
}
