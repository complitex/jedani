package ru.complitex.domain.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 21.04.2019 21:56
 */
public abstract class AbstractDomainEditModal<T extends Domain<T>> extends Modal<T> {
    public AbstractDomainEditModal(String markupId) {
        super(markupId);
    }

    public AbstractDomainEditModal(String id, IModel<T> model) {
        super(id, model);
    }

    public abstract void edit(T domain, AjaxRequestTarget target);
}
