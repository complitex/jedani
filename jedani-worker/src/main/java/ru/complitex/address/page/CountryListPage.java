package ru.complitex.address.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.address.entity.Country;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.jedani.worker.security.JedaniRoles;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 21.12.2017 1:06
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class CountryListPage extends DomainListPage{
    public CountryListPage() {
        super(Country.ENTITY_NAME, CountryEditPage.class);
    }

    @Override
    protected List<Long> getEntityAttributeIds() {
        return Arrays.asList(Country.NAME, Country.SHORT_NAME);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        entity.getEntityAttribute(Country.NAME).setDisplayCapitalize(true);
        entity.getEntityAttribute(Country.SHORT_NAME).setDisplayCapitalize(true);

        return entity.getAttributes();
    }
}
