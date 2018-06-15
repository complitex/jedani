package ru.complitex.address.page;

import ru.complitex.address.entity.City;
import ru.complitex.address.entity.Region;
import ru.complitex.domain.page.DomainListPage;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 19.12.2017 9:32
 */
public class CityListPage extends DomainListPage{
    public CityListPage() {
        super("city", "region", Region.NAME, CityEditPage.class);
    }

    @Override
    protected List<Long> getEntityAttributeIds() {
        return Arrays.asList(City.NAME, City.SHORT_NAME);
    }
}
