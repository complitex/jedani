package ru.complitex.jedani.worker.service;

import org.mybatis.cdi.Transactional;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.ValueType;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.Rule;
import ru.complitex.jedani.worker.entity.RuleAction;
import ru.complitex.jedani.worker.entity.RuleCondition;
import ru.complitex.jedani.worker.entity.SaleDecision;

import javax.inject.Inject;
import java.util.Objects;

public class SaleDecisionService {
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
                        .filter(r1 -> r1.getObjectId().equals(r.getObjectId()))
                        .findFirst()
                        .orElse(null);

                if (rule != null){
                    r.getConditions().forEach(c -> {
                        if (rule.getConditions().stream().noneMatch(c1 -> c1.getObjectId().equals(c.getObjectId()))){
                            domainService.delete(c);
                        }
                    });

                    r.getActions().forEach(a -> {
                        if (rule.getActions().stream().noneMatch(a1 -> a1.getObjectId().equals(a.getObjectId()))){
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

}
