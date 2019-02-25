package ru.complitex.common.wicket.component;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 25.02.2019 15:29
 */
public class MenuLink extends BookmarkablePageLink<Object> {
    private List<Class<? extends Page>> menuPageClasses = new ArrayList<>();

    public <C extends Page> MenuLink(String id, Class<C> pageClass) {
        super(id, pageClass);
    }

    public <C extends Page> MenuLink(String id, Class<C> pageClass, PageParameters parameters) {
        super(id, pageClass, parameters);
    }

    public MenuLink addMenuPageClass(Class<? extends Page> menuPageClass){
        menuPageClasses.add(menuPageClass);

        return this;
    }

    public List<Class<? extends Page>> getMenuPageClasses() {
        return menuPageClasses;
    }

    public boolean hasMenuPageClass(Class<? extends Page> menuPageClass){
        return menuPageClasses.stream().anyMatch(m -> m.equals(menuPageClass));
    }
}
