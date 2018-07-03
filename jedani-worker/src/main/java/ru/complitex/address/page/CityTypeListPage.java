package ru.complitex.address.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.jedani.worker.security.JedaniRoles;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 5:20
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class CityTypeListPage extends DomainListPage{
    public CityTypeListPage() {
        super("city_type", CityTypeEditPage.class);
    }
}
