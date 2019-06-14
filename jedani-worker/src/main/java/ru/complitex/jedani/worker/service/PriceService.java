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

    public SaleDecision getSaleDecision(Long storageId, Long nomenclatureId, Date date, BigDecimal total){
        if (storageId == null || nomenclatureId == null || date == null || total == null){
            return null;
        }

        List<SaleDecision> saleDecisions = saleDecisionService.getSaleDecisions(storageService.getCountryId(storageId),
                nomenclatureId, date);

        for (SaleDecision saleDecision : saleDecisions){
            saleDecisionService.loadRules(saleDecision);

            for (Rule rule : saleDecision.getRules()){
                if (saleDecisionService.check(rule, date, total)){
                    return saleDecision;
                }
            }
        }

        return null;
    }

    public BigDecimal getPrice(SaleDecision saleDecision, Date date, BigDecimal basePrice, BigDecimal total){
        if (saleDecision == null || date == null || basePrice == null || total == null){
            return basePrice;
        }

        for (Rule rule : saleDecision.getRules()){
            if (saleDecisionService.check(rule, date, total)){
                for (RuleAction a : rule.getActions()){
                    switch (RuleActionType.getValue(a.getType())){
                        case DISCOUNT:
                            Long discount = a.getNumber(RuleAction.ACTION);

                            if (discount != null && discount > 0 && discount < 100){
                                return basePrice.multiply(new BigDecimal(100 - discount)
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

    public BigDecimal getPointPrice(Long storageId, Long nomenclatureId, Date date){
        if (storageId == null || nomenclatureId == null || date == null){
            return null;
        }

        return exchangeRateService.getExchangeRateValue(storageService.getCountryId(storageId), date);
    }

    public BigDecimal getPointPrice(SaleDecision saleDecision, Date date, BigDecimal pointPrice, BigDecimal total){
        if (saleDecision == null || date == null || pointPrice == null || total == null){
            return pointPrice;
        }

        for (Rule rule : saleDecision.getRules()){
            if (saleDecisionService.check(rule, date, total)){
                for (RuleAction a : rule.getActions()){
                    if (RuleActionType.getValue(a.getType()) == RuleActionType.EURO_RATE_LESS_OR_EQUAL) {
                        BigDecimal actionPointPrice = a.getDecimal(RuleAction.ACTION);

                        return actionPointPrice != null ? pointPrice.min(actionPointPrice) : pointPrice;
                    }
                }
            }
        }

        return pointPrice;
    }
}
