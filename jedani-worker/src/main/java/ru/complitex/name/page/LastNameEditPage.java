package ru.complitex.name.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.domain.page.DomainEditPage;

/**
 * @author Anatoly A. Ivanov
 * 28.12.2017 17:33
 */
public class LastNameEditPage extends DomainEditPage{
    public LastNameEditPage(PageParameters parameters) {
        super("last_name", parameters, LastNameListPage.class);
    }
}
