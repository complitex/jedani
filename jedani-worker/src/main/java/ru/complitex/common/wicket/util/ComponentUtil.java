package ru.complitex.common.wicket.util;

import org.apache.wicket.Component;

/**
 * @author Anatoly A. Ivanov
 * 06.05.2018 23:12
 */
public class ComponentUtil {
    public static Component getAjaxParent(Component component){
        Component parent = component.getParent();

        while (parent != null){
            if (parent.getOutputMarkupId()){
                return parent;
            }

            parent = parent.getParent();
        }

        return null;
    }
}
