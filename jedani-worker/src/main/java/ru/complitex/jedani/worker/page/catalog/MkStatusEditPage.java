package ru.complitex.jedani.worker.page.catalog;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.domain.page.DomainEditPage;
import ru.complitex.jedani.worker.security.JedaniRoles;

/**
 * @author Anatoly A. Ivanov
 * 05.05.2018 8:51
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class MkStatusEditPage extends DomainEditPage {
    public MkStatusEditPage(PageParameters parameters) {
        super("mk_status", parameters, MkStatusListPage.class);
    }
}
