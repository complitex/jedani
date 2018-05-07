package ru.complitex.jedani.worker.page.directory;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.domain.page.DomainEditPage;

/**
 * @author Anatoly A. Ivanov
 * 05.05.2018 8:51
 */
public class MkStatusEditPage extends DomainEditPage {
    public MkStatusEditPage(PageParameters parameters) {
        super("mk_status", parameters, MkStatusListPage.class);
    }
}
