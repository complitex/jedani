package ru.complitex.address.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.domain.page.DomainEditPage;

/**
 * @author Anatoly A. Ivanov
 * 20.12.2017 4:00
 */
public class CityEditPage extends DomainEditPage{
    public CityEditPage(PageParameters parameters) {
        super("city", parameters, CityListPage.class);
    }
}
