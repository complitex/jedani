package ru.complitex.address.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.entity.Country;
import ru.complitex.domain.page.DomainEditPage;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 21.12.2017 1:06
 */
public class CountryEditPage extends DomainEditPage{
    public CountryEditPage(PageParameters parameters) {
        super(Country.ENTITY_NAME, parameters, CountryListPage.class);
    }

    @Override
    protected List<Long> getEntityAttributeIds() {
        return Arrays.asList(Country.NAME, Country.SHORT_NAME);
    }
}
