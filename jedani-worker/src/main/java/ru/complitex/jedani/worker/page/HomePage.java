package ru.complitex.jedani.worker.page;

import org.apache.wicket.markup.html.basic.Label;
import ru.complitex.jedani.worker.page.worker.WorkerListPage;
import ru.complitex.jedani.worker.page.worker.WorkerPage;

import java.time.LocalDateTime;

/**
 * @author Anatoly A. Ivanov
 * 21.11.2017 15:04
 */
public class HomePage extends BasePage{

    public HomePage() {
        add(new Label("test", LocalDateTime.now().toString()));

        if (isAdmin()){
            setResponsePage(WorkerListPage.class);
        }else {
            setResponsePage(WorkerPage.class);
        }
    }
}
