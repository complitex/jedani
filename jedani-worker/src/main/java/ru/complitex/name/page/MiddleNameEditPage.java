package ru.complitex.name.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.domain.page.DomainEditPage;
import ru.complitex.jedani.worker.security.JedaniRoles;

/**
 * @author Anatoly A. Ivanov
 * 28.12.2017 17:33
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class MiddleNameEditPage extends DomainEditPage{
    public MiddleNameEditPage(PageParameters parameters) {
        super("middle_name", parameters, MiddleNameListPage.class);
    }
}
