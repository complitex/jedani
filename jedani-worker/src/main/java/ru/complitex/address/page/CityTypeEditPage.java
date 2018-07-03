package ru.complitex.address.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.domain.page.DomainEditPage;
import ru.complitex.jedani.worker.security.JedaniRoles;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 5:21
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class CityTypeEditPage extends DomainEditPage{
    public CityTypeEditPage(PageParameters parameters) {
        super("city_type", parameters, CityTypeListPage.class);
    }
}
