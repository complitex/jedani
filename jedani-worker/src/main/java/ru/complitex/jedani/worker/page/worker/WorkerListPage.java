package ru.complitex.jedani.worker.page.worker;

import ru.complitex.domain.page.DomainListPage;

/**
 * @author Anatoly A. Ivanov
 * 20.12.2017 7:11
 */
public class WorkerListPage extends DomainListPage{
    public WorkerListPage() {
        super("worker", WorkerEditPage.class);
    }
}
