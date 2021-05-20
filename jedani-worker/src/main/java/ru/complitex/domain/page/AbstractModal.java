package ru.complitex.domain.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 21.04.2019 21:56
 */
public abstract class AbstractModal<T extends Domain> extends Modal<T> {
    public AbstractModal(String markupId) {
        super(markupId);
    }

    public AbstractModal(String id, IModel<T> model) {
        super(id, model);
    }

    public abstract void create(T domain, AjaxRequestTarget target);

    public abstract void edit(T domain, AjaxRequestTarget target);
}
