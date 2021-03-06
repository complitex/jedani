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

    @Inject
    private SaleService saleService;

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
                                        Long installmentMonths, boolean yourself, Long quantity, Long paymentPercent){
        if (storageId == null || nomenclatureId == null || date == null || total == null){
            return null;
        }

        List<SaleDecision> saleDecisions = saleDecisionService.getSaleDecisions(storageService.getCountryId(storageId),
                nomenclatureId, date);

        for (SaleDecision saleDecision : saleDecisions){
            saleDecisionService.loadRules(saleDecision);

            for (Rule rule : saleDecision.getRules()){
                if (saleDecisionService.check(rule, date, total, installmentMonths, yourself, quantity, paymentPercent)){
                    return saleDecision;
                }
            }
        }

        return null;
    }

    public BigDecimal getPrice(SaleDecision saleDecision, Date date, BigDecimal basePrice, BigDecimal total,
                               Long installmentMonths, boolean yourself, Long quantity, Long paymentPercent){
        if (saleDecision == null || date == null || basePrice == null || total == null){
            return basePrice;
        }

        for (Rule rule : saleDecision.getRules()){
            if (saleDecisionService.check(rule, date, total, installmentMonths, yourself, quantity, paymentPercent)){
                for (RuleAction a : rule.getActions()){
                    switch (IActionType.getValue(SaleDecisionActionType.class, a.getType())){
                        case DISCOUNT:
                            BigDecimal discount = a.getDecimal(RuleAction.ACTION);

                            BigDecimal BD_100 = new BigDecimal(100);

                            if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0 &&
                                    discount.compareTo(BD_100) <= 0){
                                return basePrice.multiply(BD_100.subtract(discount))
                                        .divide(BD_100, 2, RoundingMode.HALF_EVEN);
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

    public BigDecimal getRate(Long storageId, Date date){
        if (storageId == null || date == null){
            return null;
        }

        return exchangeRateService.getExchangeRate(storageService.getCountryId(storageId), date);
    }

    public BigDecimal getRate(SaleDecision saleDecision, Date paymentDate, BigDecimal rate, BigDecimal total,
                              Long installmentMonths, boolean yourself, Long quantity, Long paymentPercent){
        if (saleDecision == null || paymentDate == null || rate == null || total == null){
            return rate;
        }

        for (Rule rule : saleDecision.getRules()){
            if (saleDecisionService.check(rule, paymentDate, total, installmentMonths, yourself, quantity, paymentPercent)){
                for (RuleAction a : rule.getActions()){
                    if (IActionType.getValue(SaleDecisionActionType.class, a.getType()) ==
                            SaleDecisionActionType.EURO_RATE_LESS_OR_EQUAL) {
                        BigDecimal actionRate = a.getDecimal(RuleAction.ACTION);

                        return actionRate != null ? rate.min(actionRate) : rate;
                    }
                }
            }
        }

        return rate;
    }

    public BigDecimal getRate(Long storageId, SaleDecision saleDecision, Date paymentDate,
                              BigDecimal total, Long installmentMonths, boolean yourself, Long quantity, Long paymentPercent){
        BigDecimal rate = getRate(storageId, paymentDate);

        if (saleDecision != null){
            return getRate(saleDecision, paymentDate, rate, total, installmentMonths, yourself, quantity, paymentPercent);
        }

        return rate;
    }

    public BigDecimal getRate(Sale sale, SaleItem saleItem, Date date){
        BigDecimal rate = getRate(sale.getStorageId(), date);

        return getRate(saleDecisionService.getSaleDecision(saleItem.getSaleDecisionId()), date, rate, sale.getTotal(),
                sale.getInstallmentMonths(), sale.isForYourself(), saleItem.getQuantity(), saleService.getPaymentPercent(sale).longValue());
    }
}
