package ru.complitex.address.page;

import ru.complitex.domain.page.DomainListPage;

/**
 * @author Anatoly A. Ivanov
 * 21.12.2017 1:06
 */
public class CountryListPage extends DomainListPage{
    public CountryListPage() {
        super("country", CountryEditPage.class);
    }
}
