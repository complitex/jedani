package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Parameter;
import ru.complitex.jedani.worker.entity.RewardParameter;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static ru.complitex.jedani.worker.security.JedaniRoles.ADMINISTRATORS;

/**
 * @author Anatoly A. Ivanov
 * 21.11.2019 10:03 AM
 */
@AuthorizeInstantiation({ADMINISTRATORS})
public class RewardParameterListPage extends DomainListModalPage<RewardParameter> {
    @Inject
    private DomainService domainService;

    public RewardParameterListPage() {
        super(RewardParameter.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(RewardParameter.PARAMETER).setRequired(true).withReference(Parameter.class, Parameter.NAME));
        list.add(entity.getEntityAttribute(RewardParameter.DATE_BEGIN));
        list.add(entity.getEntityAttribute(RewardParameter.DATE_END));
        list.add(entity.getEntityAttribute(RewardParameter.VALUE).setRequired(true));

        return list;
    }

    @Override
    protected boolean checkUnique(Domain domain) {
        Date begin = domain.getDate(RewardParameter.DATE_BEGIN);
        Date end = domain.getDate(RewardParameter.DATE_END);

        return domainService.getDomains(RewardParameter.class, FilterWrapper.of(new RewardParameter()
                        .setParameterId(domain.getNumber(RewardParameter.PARAMETER)))).stream()
                .filter(rewardParameter -> !Objects.equals(rewardParameter.getObjectId(), domain.getObjectId()))
                .filter(rewardParameter -> rewardParameter.getEnd() != null || begin == null ||
                        (rewardParameter.getBegin() != null && rewardParameter.getBegin().compareTo(begin) > 0))
                .noneMatch(rewardParameter -> {
                    Date b = rewardParameter.getBegin();
                    Date e = rewardParameter.getEnd();

                    return (b == null || end == null || b.before(end)) && (e == null || end != null && e.after(end)) ||
                            (begin == null || e == null || begin.before(e)) && (end == null || e != null && end.after(e));
                });
    }

    @Override
    protected void onSave(Domain domain) {
        Date begin = domain.getDate(RewardParameter.DATE_BEGIN);

        domainService.getDomains(RewardParameter.class, FilterWrapper.of(new RewardParameter()
                .setParameterId(domain.getNumber(RewardParameter.PARAMETER)))).stream()
                .filter(rewardParameter -> !Objects.equals(rewardParameter.getObjectId(), domain.getObjectId()))
                .filter(rewardParameter -> rewardParameter.getBegin() == null || rewardParameter.getBegin().compareTo(begin) <= 0)
                .filter(rewardParameter -> rewardParameter.getEnd() == null)
                .forEach(rewardParameter -> {
                    rewardParameter.setEnd(begin);

                    domainService.save(rewardParameter);
                });
    }
}
