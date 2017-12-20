package ru.complitex.address.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.domain.page.DomainEditPage;

/**
 * @author Anatoly A. Ivanov
 * 21.12.2017 1:06
 */
public class CountryEditPage extends DomainEditPage{
    public CountryEditPage(PageParameters parameters) {
        super("country", parameters, CountryListPage.class);
    }
}
