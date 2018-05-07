package ru.complitex.jedani.worker.page.directory;

import org.apache.wicket.markup.html.WebPage;
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
