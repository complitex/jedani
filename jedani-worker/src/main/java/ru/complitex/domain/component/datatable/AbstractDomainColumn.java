package ru.complitex.domain.component.datatable;

import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilteredColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.EntityAttribute;

/**
 * @author Anatoly A. Ivanov
 * 20.12.2017 3:20
 */
public abstract class AbstractDomainColumn<T extends Domain>  extends AbstractColumn<T, SortProperty>
        implements IFilteredColumn<T, SortProperty> {

    public AbstractDomainColumn() {
        super(Model.of(""), null);
    }

    public AbstractDomainColumn(IModel<String> displayModel, SortProperty sortProperty) {
        super(displayModel, sortProperty);
    }

    public AbstractDomainColumn(EntityAttribute entityAttribute){
        super(displayModel(entityAttribute), sortProperty(entityAttribute));
    }

    protected static Model<String> displayModel(EntityAttribute entityAttribute) {
        if (entityAttribute != null){
            return Model.of(entityAttribute.getValueText());

        }

        return Model.of("");
    }

    protected static SortProperty sortProperty(EntityAttribute entityAttribute){
        if (entityAttribute != null){
            return new SortProperty(entityAttribute.getValueType().getKey(), entityAttribute);
        }

        return null;
    }
}
