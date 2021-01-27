package ru.complitex.domain.component.datatable;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.table.FilterForm;
import ru.complitex.common.wicket.table.IFilterColumn;
import ru.complitex.common.wicket.table.TextFilter;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.EntityAttribute;

/**
 * @author Anatoly A. Ivanov
 * 20.12.2017 3:20
 */
public abstract class AbstractDomainColumn<T extends Domain>  extends AbstractColumn<T, SortProperty>
        implements IFilterColumn<T, SortProperty> {

    private String columnKey;

    private Integer size;

    public AbstractDomainColumn() {
        super(Model.of(""), null);
    }

    public AbstractDomainColumn(IModel<String> displayModel, SortProperty sortProperty) {
        super(displayModel, sortProperty);
    }

    public AbstractDomainColumn(EntityAttribute entityAttribute){
        super(displayModel(entityAttribute), sortProperty(entityAttribute));
    }

    public AbstractDomainColumn(String columnKey) {
        super(new ResourceModel(columnKey), new SortProperty(columnKey));

        this.columnKey = columnKey;
    }

    public AbstractDomainColumn(String columnKey, Integer size) {
        this(columnKey);

        this.size = size;
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

    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        TextFilter textDataFilter =  new TextFilter<>(componentId, new PropertyModel<>(form.getModel(),
                "map." + columnKey), form);

        if (size != null){
            textDataFilter.setSize(size);
        }

        return textDataFilter;
    }
}
