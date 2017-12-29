package ru.complitex.name.page;

import ru.complitex.domain.page.DomainListPage;

/**
 * @author Anatoly A. Ivanov
 * 28.12.2017 17:32
 */
public class MiddleNameListPage extends DomainListPage{
    public MiddleNameListPage() {
        super("middle_name", MiddleNameEditPage.class);
    }
}
