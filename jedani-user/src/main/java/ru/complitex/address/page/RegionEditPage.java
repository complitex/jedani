package ru.complitex.address.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.domain.page.DomainEditPage;

/**
 * @author Anatoly A. Ivanov
 * 18.12.2017 0:21
 */
public class RegionEditPage extends DomainEditPage{
    public RegionEditPage(PageParameters parameters) {
        super("region", parameters, RegionListPage.class);
    }
}
