package ru.complitex.jedani.worker.page.catalog;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.RewardType;
import ru.complitex.jedani.worker.security.JedaniRoles;

@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class RewardTypeListPage extends DomainListModalPage<RewardType> {
    public RewardTypeListPage() {
        super(RewardType.class);
    }
}
