package ru.complitex.address.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.address.entity.Country;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.security.JedaniRoles;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 21.12.2017 1:06
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class CountryListPage extends DomainListModalPage<Country> {
    public CountryListPage() {
        super(Country.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        return entity.getEntityAttributes(Country.NAME, Country.SHORT_NAME);
    }
}
