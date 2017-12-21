package ru.complitex.address.page;

import ru.complitex.domain.page.DomainListPage;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 5:20
 */
public class CityTypeListPage extends DomainListPage{
    public CityTypeListPage() {
        super("city_type", CityTypeEditPage.class);
    }
}
