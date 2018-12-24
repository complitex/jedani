package ru.complitex.jedani.worker.page;

import ru.complitex.jedani.worker.page.worker.WorkerListPage;
import ru.complitex.jedani.worker.page.worker.WorkerPage;

/**
 * @author Anatoly A. Ivanov
 * 21.11.2017 15:04
 */
public class HomePage extends BasePage{

    public HomePage() {
        if (isAdmin()){
            setResponsePage(WorkerListPage.class);
        }else {
            setResponsePage(WorkerPage.class);
        }
    }
}
