package ru.complitex.jedani.worker.page.directory;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.domain.page.DomainEditPage;

/**
 * @author Anatoly A. Ivanov
 * 05.05.2018 9:51
 */
public class PositionEditPage extends DomainEditPage {
    public PositionEditPage(PageParameters parameters) {
        super("position", parameters, PositionListPage.class);
    }
}
