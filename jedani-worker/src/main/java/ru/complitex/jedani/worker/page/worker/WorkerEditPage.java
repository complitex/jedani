package ru.complitex.jedani.worker.page.worker;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.domain.page.DomainEditPage;

/**
 * @author Anatoly A. Ivanov
 * 20.12.2017 7:11
 */
public class WorkerEditPage extends DomainEditPage{
    public WorkerEditPage(PageParameters parameters) {
        super("worker", parameters, WorkerListPage.class);
    }
}
