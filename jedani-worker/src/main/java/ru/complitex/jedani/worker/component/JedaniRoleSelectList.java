package ru.complitex.jedani.worker.component;

import org.apache.wicket.model.IModel;
import ru.complitex.domain.component.form.RoleSelectList;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 13.12.2018 21:14
 */
public class JedaniRoleSelectList extends RoleSelectList {
    public JedaniRoleSelectList(String id, IModel<List<String>> model, List<String> roles) {
        super(id, model, roles);
    }

    @Override
    protected String getDisplayValue(String object) {
        return getString(object);
    }
}
