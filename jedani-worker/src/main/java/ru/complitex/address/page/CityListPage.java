package ru.complitex.address.page;

import ru.complitex.domain.page.DomainListPage;

/**
 * @author Anatoly A. Ivanov
 * 19.12.2017 9:32
 */
public class CityListPage extends DomainListPage{
    public CityListPage() {
        super("city", CityEditPage.class);
    }
}
