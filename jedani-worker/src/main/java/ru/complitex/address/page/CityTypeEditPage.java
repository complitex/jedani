package ru.complitex.address.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.domain.page.DomainEditPage;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 5:21
 */
public class CityTypeEditPage extends DomainEditPage{
    public CityTypeEditPage(PageParameters parameters) {
        super("city_type", parameters, CityTypeListPage.class);
    }
}
