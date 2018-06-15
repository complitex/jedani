package ru.complitex.address.page;

import ru.complitex.address.entity.Country;
import ru.complitex.domain.page.DomainListPage;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 21.12.2017 1:06
 */
public class CountryListPage extends DomainListPage{
    public CountryListPage() {
        super(Country.ENTITY_NAME, CountryEditPage.class);
    }

    @Override
    protected List<Long> getEntityAttributeIds() {
        return Arrays.asList(Country.NAME, Country.SHORT_NAME);
    }
}
