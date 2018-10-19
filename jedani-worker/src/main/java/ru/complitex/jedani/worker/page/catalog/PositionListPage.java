package ru.complitex.jedani.worker.page.catalog;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.jedani.worker.entity.Position;
import ru.complitex.jedani.worker.security.JedaniRoles;

/**
 * @author Anatoly A. Ivanov
 * 05.05.2018 9:51
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class PositionListPage extends DomainListPage<Position> {
    public PositionListPage() {
        super(Position.class, PositionEditPage.class);
    }
}
