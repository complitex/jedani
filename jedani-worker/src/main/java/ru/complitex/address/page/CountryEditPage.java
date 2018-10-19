package ru.complitex.address.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.entity.Country;
import ru.complitex.domain.page.DomainEditPage;
import ru.complitex.jedani.worker.security.JedaniRoles;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 21.12.2017 1:06
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class CountryEditPage extends DomainEditPage{
    public CountryEditPage(PageParameters parameters) {
        super(Country.ENTITY_NAME, parameters, CountryListPage.class);
    }

    @Override
    protected List<Long> getEntityAttributeIds() {
        return Arrays.asList(Country.NAME, Country.SHORT_NAME);
    }
}
