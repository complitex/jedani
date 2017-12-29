package ru.complitex.name.page;

import ru.complitex.domain.page.DomainListPage;

/**
 * @author Anatoly A. Ivanov
 * 28.12.2017 17:32
 */
public class FirstNameListPage extends DomainListPage{
    public FirstNameListPage() {
        super("first_name", FirstNameEditPage.class);
    }
}
