package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Parameter;
import ru.complitex.jedani.worker.entity.RewardParameter;

import java.util.ArrayList;
import java.util.List;

import static ru.complitex.jedani.worker.security.JedaniRoles.ADMINISTRATORS;

/**
 * @author Anatoly A. Ivanov
 * 21.11.2019 10:03 AM
 */
@AuthorizeInstantiation({ADMINISTRATORS})
public class RewardParameterListPage extends DomainListModalPage<RewardParameter> {
    public RewardParameterListPage() {
        super(RewardParameter.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(RewardParameter.PARAMETER).withReference(Parameter.class, Parameter.NAME));
        list.add(entity.getEntityAttribute(RewardParameter.DATE_BEGIN));
        list.add(entity.getEntityAttribute(RewardParameter.DATE_END));
        list.add(entity.getEntityAttribute(RewardParameter.VALUE));

        return list;
    }
}
