package ru.complitex.jedani.worker.page.catalog;

import ru.complitex.domain.page.DomainListPage;

/**
 * @author Anatoly A. Ivanov
 * 05.05.2018 9:51
 */
public class PositionListPage extends DomainListPage {
    public PositionListPage() {
        super("position", PositionEditPage.class);
    }
}
