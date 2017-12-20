package ru.complitex.jedani.worker.page;

import org.apache.wicket.markup.html.basic.Label;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 * 21.11.2017 15:04
 */
public class HomePage extends BasePage{

    public HomePage() {
        add(new Label("test", new Date()));
    }


}
