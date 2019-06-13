package ru.complitex.jedani.worker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.jedani.worker.entity.Price;
import ru.complitex.jedani.worker.entity.RuleAction;
import ru.complitex.jedani.worker.entity.RuleActionType;
import ru.complitex.jedani.worker.entity.SaleDecision;
import ru.complitex.jedani.worker.mapper.PriceMapper;

import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
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

    public SaleDecision getSaleDecision(BigDecimal basePrice, Long storageId, Long nomenclatureId, Date date, BigDecimal total){
        //todo add get sale decisions

        return null;
    }

    public BigDecimal calculatePrice(SaleDecision saleDecision, BigDecimal basePrice, Long storageId,
                                     Long nomenclatureId, Date date, BigDecimal total){
        //todo add calculate price

        return null;
    }

    public BigDecimal getPrice(Long storageId, Long nomenclatureId, Date date, BigDecimal total){
        if (storageId == null || nomenclatureId == null || date == null){
            return null;
        }

        BigDecimal basePrice = getBasePrice(storageId, nomenclatureId, date);

        return calculatePrice(basePrice, storageId, nomenclatureId, date, total);
    }

    public BigDecimal calculatePrice(BigDecimal basePrice, Long storageId, Long nomenclatureId, Date date, BigDecimal total){
        List<SaleDecision> saleDecisions = saleDecisionService.getSaleDecisions(storageService.getCountryId(storageId),
                nomenclatureId, date);

        List<BigDecimal> prices = new ArrayList<>();

        saleDecisions.forEach(sd -> {
            saleDecisionService.loadRules(sd);

            sd.getRules().forEach(r -> {
                if (saleDecisionService.check(r, date, total)){
                    for (RuleAction a : r.getActions()){
                        switch (RuleActionType.getValue(a.getType())){
                            case DISCOUNT:
                                Long discount = a.getNumber(RuleAction.ACTION);

                                if (discount != null && discount > 0 && discount < 100){
                                    prices.add(basePrice.multiply(new BigDecimal(discount/100).setScale(2,
                                            RoundingMode.HALF_EVEN)));
                                }

                                break;
                            case PRICE:
                                BigDecimal price = a.getDecimal(RuleAction.ACTION);

                                if (price != null){
                                    prices.add(price);
                                }

                                break;
                        }
                    }
                }
            });
        });

        return prices.stream().min(Comparator.naturalOrder()).orElse(basePrice);
    }

    public BigDecimal getPointPrice(Long storageId, Long nomenclatureId, Date date){
        if (storageId == null || nomenclatureId == null || date == null){
            return null;
        }

        BigDecimal basePointPrice = exchangeRateService.getExchangeRateValue(storageService.getCountryId(storageId), date);

        return calculatePointPrice(basePointPrice, storageId, nomenclatureId, date);
    }

    public BigDecimal calculatePointPrice(BigDecimal basePointPrice, Long storageId, Long nomenclatureId, Date date){
        return basePointPrice;
    }

    public BigDecimal getLocalPrice(){
        return null;
    }
}
