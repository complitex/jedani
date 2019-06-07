package ru.complitex.jedani.worker.service;

import org.mybatis.cdi.Transactional;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.ValueType;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.mapper.SaleDecisionMapper;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SaleDecisionService {
    @Inject
    private SaleDecisionMapper saleDecisionMapper;

    @Inject
    private EntityService entityService;

    @Inject
    private DomainService domainService;

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

        domainService.save(saleDecision);

        saleDecision.getRules().forEach(r -> {
            r.setParentEntityId(entityService.getEntity(SaleDecision.ENTITY_NAME).getId());
            r.setParentId(saleDecision.getObjectId());

            domainService.save(r);

            r.getConditions().forEach(c -> {
                c.setParentEntityId(entityService.getEntity(Rule.ENTITY_NAME).getId());
                c.setParentId(r.getObjectId());

                updateValues(c, RuleCondition.VALUE_TYPE, RuleCondition.CONDITION, RuleCondition.COMPARATOR);

                domainService.save(c);
            });

            r.getActions().forEach(a -> {
                a.setParentEntityId(entityService.getEntity(Rule.ENTITY_NAME).getId());
                a.setParentId(r.getObjectId());

                updateValues(a, RuleAction.VALUE_TYPE, RuleAction.ACTION, RuleAction.COMPARATOR);

                domainService.save(a);
            });
        });
    }

    public void loadRules(SaleDecision saleDecision){
        saleDecision.setRules(domainService.getDomainsByParentId(Rule.class, saleDecision.getObjectId()));

        saleDecision.getRules().forEach(r -> {
            r.setConditions(domainService.getDomainsByParentId(RuleCondition.class, r.getObjectId()));
            r.setActions(domainService.getDomainsByParentId(RuleAction.class, r.getObjectId()));
        });
    }

    private void updateValues(Domain domain, Long valueTypeEAId, Long valueEAId, Long comparatorEAId){
        if (Objects.equals(domain.getNumber(valueTypeEAId), ValueType.BOOLEAN.getId())){
            domain.setText(valueEAId, null);
            domain.setDate(valueEAId, null);
            domain.setNumber(comparatorEAId, null);
        }else if (Objects.equals(domain.getNumber(valueTypeEAId), ValueType.DECIMAL.getId())){
            domain.setNumber(valueEAId, null);
            domain.setDate(valueEAId, null);
        }else if (Objects.equals(domain.getNumber(valueTypeEAId), ValueType.NUMBER.getId())){
            domain.setText(valueEAId, null);
            domain.setDate(valueEAId, null);
        }else if (Objects.equals(domain.getNumber(valueTypeEAId), ValueType.DATE.getId())){
            domain.setNumber(valueEAId, null);
            domain.setText(valueEAId, null);
        }
    }

    public List<SaleDecision> getSaleDecisions(Long countryId, Long nomenclatureId, Date date){
        return saleDecisionMapper.getSaleDecisions(FilterWrapper.of(new SaleDecision()
                .setCountryId(countryId))
                .put(SaleDecision.FILTER_NOMENCLATURE, nomenclatureId)
                .put(SaleDecision.FILTER_DATE, date));

    }

    public boolean check(Rule rule, Date date, BigDecimal total){
        boolean check = true;

        for (RuleCondition condition : rule.getConditions()){
            if (RuleConditionType.PAYMENT_DATE.getId().equals(condition.getType())){
                Date conditionDate = condition.getDate(RuleCondition.CONDITION);

                if (condition.getComparator() != null){
                    switch (RuleConditionComparator.getValue(condition.getComparator())){
                        case EQUAL:
                            check = date.compareTo(conditionDate) == 0;
                            break;
                        case NOT_EQUAL:
                            check = date.compareTo(conditionDate) != 0;
                            break;
                        case GREATER:
                            check = date.compareTo(conditionDate) > 0;
                            break;
                        case LOWER:
                            check = date.compareTo(conditionDate) < 0;
                            break;
                        case GREATER_OR_EQUAL:
                            check = date.compareTo(conditionDate) >= 0;
                            break;
                        case LOWER_OR_EQUAL:
                            check = date.compareTo(conditionDate) <= 0;
                            break;
                    }
                }

                if (!check){
                    break;
                }
            }
        }

        return check;
    }
}
