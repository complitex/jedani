package ru.complitex.jedani.worker.page.catalog;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.MkStatus;
import ru.complitex.jedani.worker.security.JedaniRoles;

/**
 * @author Anatoly A. Ivanov
 * 05.05.2018 8:49
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class MkStatusListPage extends DomainListModalPage<MkStatus> {
    public MkStatusListPage() {
        super(MkStatus.class);
    }
}
