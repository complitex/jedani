package ru.complitex.domain.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

/**
 * @author Anatoly A. Ivanov
 * 21.04.2019 21:56
 */
public abstract class AbstractModal<T> extends Modal<T> {
    public AbstractModal(String markupId) {
        super(markupId);
    }

    public AbstractModal(String id, IModel<T> model) {
        super(id, model);
    }

    public abstract void create(AjaxRequestTarget target);

    public abstract void edit(T object, AjaxRequestTarget target);
}
