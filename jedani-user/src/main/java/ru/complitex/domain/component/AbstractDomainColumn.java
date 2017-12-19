package ru.complitex.domain.component;

import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilteredColumn;
import org.apache.wicket.model.IModel;
import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 20.12.2017 3:20
 */
public abstract class AbstractDomainColumn  extends AbstractColumn<Domain, String> implements IFilteredColumn<Domain, String> {

    public AbstractDomainColumn(IModel<String> displayModel, String sortProperty) {
        super(displayModel, sortProperty);
    }

    public AbstractDomainColumn(IModel<String> displayModel) {
        super(displayModel);
    }
}
