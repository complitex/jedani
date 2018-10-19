package ru.complitex.jedani.worker.page.catalog;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.domain.page.DomainEditPage;
import ru.complitex.jedani.worker.entity.Position;
import ru.complitex.jedani.worker.security.JedaniRoles;

/**
 * @author Anatoly A. Ivanov
 * 05.05.2018 9:51
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class PositionEditPage extends DomainEditPage {
    public PositionEditPage(PageParameters parameters) {
        super(Position.ENTITY_NAME, parameters, PositionListPage.class);
    }
}
