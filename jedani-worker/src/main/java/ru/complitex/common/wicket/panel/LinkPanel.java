package ru.complitex.common.wicket.panel;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author Anatoly A. Ivanov
 * 28.07.2017 17:00
 */
public class LinkPanel extends Panel {
    public static String COMPONENT_ID = "link";

    public LinkPanel(String id, Component link) {
        super(id);

        add(link);
    }
}
