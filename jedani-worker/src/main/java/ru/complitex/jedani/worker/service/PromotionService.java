package ru.complitex.jedani.worker.service;

import org.mybatis.cdi.Transactional;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.Promotion;
import ru.complitex.jedani.worker.entity.Rule;
import ru.complitex.jedani.worker.entity.RuleAction;
import ru.complitex.jedani.worker.entity.RuleCondition;
import ru.complitex.jedani.worker.util.Rules;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 01.10.2019 4:50 PM
 */
public class PromotionService {
    @Inject
    private EntityService entityService;

    @Inject
    private DomainService domainService;

    @Transactional
    public void save(Promotion promotion){
        if (promotion.getObjectId() != null){
            Promotion p = new Promotion();
            
            p.setObjectId(promotion.getObjectId());

            loadRules(p);

            p.getRules().forEach(r -> {
                Rule rule = promotion.getRules().stream()
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

        promotion.setDate(Promotion.DATE_BEGIN, Dates.atStartOfDay(promotion.getDate(Promotion.DATE_BEGIN)));
        promotion.setDate(Promotion.DATE_END, Dates.atEndOfDay(promotion.getDate(Promotion.DATE_END)));

        domainService.save(promotion);

        promotion.getRules().forEach(r -> {
            r.setParentEntityId(entityService.getEntity(Promotion.ENTITY_NAME).getId());
            r.setParentId(promotion.getObjectId());

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
    
    public void loadRules(Promotion promotion){
        promotion.setRules(domainService.getDomainsByParentId(Rule.class, Promotion.class,
                promotion.getObjectId()));

        promotion.getRules().forEach(r -> {
            r.setConditions(domainService.getDomainsByParentId(RuleCondition.class, r.getObjectId()));
            r.setActions(domainService.getDomainsByParentId(RuleAction.class, r.getObjectId()));
        });
    }
}
