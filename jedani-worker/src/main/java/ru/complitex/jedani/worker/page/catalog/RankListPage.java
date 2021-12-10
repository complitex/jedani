package ru.complitex.jedani.worker.page.catalog;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Rank;
import ru.complitex.jedani.worker.security.JedaniRoles;

@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class RankListPage extends DomainListModalPage<Rank> {
    public RankListPage() {
        super(Rank.class);
    }

    @Override
    protected boolean isEditEnabled() {
        return false;
    }
}
