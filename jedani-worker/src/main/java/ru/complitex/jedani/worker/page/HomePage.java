package ru.complitex.jedani.worker.page;

import ru.complitex.jedani.worker.page.worker.StructurePage;
import ru.complitex.jedani.worker.page.worker.WorkerPage;

/**
 * @author Anatoly A. Ivanov
 * 21.11.2017 15:04
 */
public class HomePage extends BasePage{

    public HomePage() {
        if (isAdmin()){
            setResponsePage(StructurePage.class);
        }else {
            setResponsePage(WorkerPage.class);
        }
    }
}
