package ru.complitex.jedani.web;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import ru.complitex.jedani.web.catalog.ImportPage;

/**
 * @author Anatoly A. Ivanov
 * 22.11.2017 16:58
 */
public class BasePage extends WebPage{
    public BasePage() {
        add(new BookmarkablePageLink<>("import", ImportPage.class));
    }
}
