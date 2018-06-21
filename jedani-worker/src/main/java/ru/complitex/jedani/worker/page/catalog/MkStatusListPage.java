package ru.complitex.jedani.worker.page.catalog;

import ru.complitex.domain.page.DomainListPage;

/**
 * @author Anatoly A. Ivanov
 * 05.05.2018 8:49
 */
public class MkStatusListPage extends DomainListPage {
    public MkStatusListPage() {
        super("mk_status", MkStatusEditPage.class);
    }
}
