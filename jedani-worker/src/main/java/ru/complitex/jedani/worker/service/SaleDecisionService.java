package ru.complitex.jedani.worker.service;

import org.mybatis.cdi.Transactional;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.mapper.SaleDecisionMapper;
import ru.complitex.jedani.worker.util.Rules;

import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class SaleDecisionService implements Serializable {
    @Inject
    private SaleDecisionMapper saleDecisionMapper;

    @Inject
    private EntityService entityService;

    @Inject
    private DomainService domainService;

    public SaleDecision getSaleDecision(Long objectId){
        if (objectId == null){
            return null;
        }

        SaleDecision saleDecision = domainService.getDomain(SaleDecision.class, objectId);

        loadRules(saleDecision);

        return saleDecision;
    }

    @Transactional
    public void save(SaleDecision saleDecision){
        if (saleDecision.getObjectId() != null){
            SaleDecision sd = new SaleDecision();
            sd.setObjectId(saleDecision.getObjectId());

            loadRules(sd);

            sd.getRules().forEach(r -> {
                Rule rule = saleDecision.getRules().stream()
                        .filter(r1 -> r.getObjectId().equals(r1.getObjectId()))
                        .findFirst()
                        .orElse(null);

                if (rule != null){
                    r.getConditions().forEach(c -> {
                        if (rule.getConditions().stream().noneMatch(c1 -> c.getObjectId().equals(c1.getObjectId()))){
                            domainService.delete(c);
                        }
                    });

                    r.getActions().forEach(a -> {
                        if (rule.getActions().stream().noneMatch(a1 -> a.getObjectId().equals(a1.getObjectId()))){
                            domainService.delete(a);
                        }
                    });
                }else{
                    domainService.delete(r);
                }
            });
        }

        saleDecision.setDate(SaleDecision.DATE_BEGIN, Dates.atStartOfDay(saleDecision.getDate(SaleDecision.DATE_BEGIN)));
        saleDecision.setDate(SaleDecision.DATE_END, Dates.atEndOfDay(saleDecision.getDate(SaleDecision.DATE_END)));

        domainService.save(saleDecision);

        saleDecision.getRules().forEach(r -> {
            r.setParentEntityId(entityService.getEntity(SaleDecision.ENTITY_NAME).getId());
            r.setParentId(saleDecision.getObjectId());

            domainService.save(r);

            r.getConditions().forEach(c -> {
                c.setParentEntityId(entityService.getEntity(Rule.ENTITY_NAME).getId());
                c.setParentId(r.getObjectId());

                Rules.updateValues(c, RuleCondition.VALUE_TYPE, RuleCondition.CONDITION, RuleCondition.COMPARATOR);

                domainService.save(c);
            });

            r.getActions().forEach(a -> {
                a.setParentEntityId(entityService.getEntity(Rule.ENTITY_NAME).getId());
                a.setParentId(r.getObjectId());

                Rules.updateValues(a, RuleAction.VALUE_TYPE, RuleAction.ACTION, RuleAction.COMPARATOR);

                domainService.save(a);
            });
        });
    }

    public void loadRules(SaleDecision saleDecision){
        saleDecision.setRules(domainService.getDomainsByParentId(Rule.class, SaleDecision.class,
                saleDecision.getObjectId()));

        saleDecision.getRules().forEach(r -> {
            r.setConditions(domainService.getDomainsByParentId(RuleCondition.class, r.getObjectId()));
            r.setActions(domainService.getDomainsByParentId(RuleAction.class, r.getObjectId()));
        });
    }

    public List<SaleDecision> getSaleDecisions(Long countryId, Long nomenclatureId, Date date){
        return saleDecisionMapper.getSaleDecisions(FilterWrapper.of(new SaleDecision()
                .setCountryId(countryId))
                .put(SaleDecision.FILTER_NOMENCLATURE, nomenclatureId)
                .put(SaleDecision.FILTER_DATE, date));

    }

    public boolean check(Rule rule, Date paymentDate, BigDecimal total, Long installmentMonths, boolean youself,
                         Long quantity, Long paymentPercent){
        for (RuleCondition ruleCondition : rule.getConditions()){
            if (SaleDecisionConditionType.PAYMENT_DATE.getId().equals(ruleCondition.getType()) &&
                    !isCheck(ruleCondition, ruleCondition.getDate(RuleCondition.CONDITION), paymentDate)){
                return false;
            }else if (SaleDecisionConditionType.PAYMENT_TOTAL.getId().equals(ruleCondition.getType()) &&
                    !isCheck(ruleCondition, ruleCondition.getDecimal(RuleCondition.CONDITION), total)){
                return false;
            }else if (paymentPercent != null &&
                    SaleDecisionConditionType.PAYMENT_PERCENT.getId().equals(ruleCondition.getType()) &&
                    !isCheck(ruleCondition, ruleCondition.getNumber(RuleCondition.CONDITION), paymentPercent)){
                return false;
            }else if (SaleDecisionConditionType.PAYMENT_PERIOD_MONTH.getId().equals(ruleCondition.getType()) &&
                    !isCheck(ruleCondition, ruleCondition.getNumber(RuleCondition.CONDITION), installmentMonths)){
                return false;
            }else if (SaleDecisionConditionType.FOR_YOURSELF.getId().equals(ruleCondition.getType())){
                return youself == ruleCondition.isBoolean(RuleCondition.CONDITION);
            }else if (SaleDecisionConditionType.QUANTITY.getId().equals(ruleCondition.getType()) &&
                    !isCheck(ruleCondition, ruleCondition.getNumber(RuleCondition.CONDITION), quantity)){
                return false;
            }
        }

        return true;
    }

    private <T extends Comparable<T>> boolean isCheck(RuleCondition ruleCondition, T condition, T object){
        if (condition != null && object != null && ruleCondition.getComparator() != null){
            switch (RuleConditionComparator.getValue(ruleCondition.getComparator())){
                case EQUAL:
                    if (condition instanceof Date && object instanceof Date){
                        return Dates.isSameDay((Date)condition, (Date)object);
                    }

                    return object.compareTo(condition) == 0;
                case NOT_EQUAL:
                    return object.compareTo(condition) != 0;
                case GREATER:
                    return object.compareTo(condition) > 0;
                case LOWER:
                    return object.compareTo(condition) < 0;
                case GREATER_OR_EQUAL:
                    return object.compareTo(condition) >= 0;
                case LOWER_OR_EQUAL:
                    return object.compareTo(condition) <= 0;
            }
        }

        return true;
    }
}
