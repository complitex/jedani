package ru.complitex.name.page;

import ru.complitex.domain.page.DomainListPage;

/**
 * @author Anatoly A. Ivanov
 * 28.12.2017 17:32
 */
public class LastNameListPage extends DomainListPage{
    public LastNameListPage() {
        super("last_name", LastNameEditPage.class);
    }
}
