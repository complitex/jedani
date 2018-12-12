package ru.complitex.common.wicket.panel;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author Anatoly A. Ivanov
 * 12.12.2018 20:06
 */
public class SelectPanel extends Panel {
    public static String SELECT_COMPONENT_ID = "select";

    public SelectPanel(String id, Component select) {
        super(id);

        add(select);
    }
}
