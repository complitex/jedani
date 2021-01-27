package ru.complitex.domain.component.datatable;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.entity.Sort;
import ru.complitex.common.wicket.table.Column;
import ru.complitex.common.wicket.table.FilterForm;
import ru.complitex.common.wicket.table.TextFilter;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.EntityAttribute;

/**
 * @author Anatoly A. Ivanov
 * 20.12.2017 3:20
 */
public abstract class AbstractDomainColumn<T extends Domain<T>> extends Column<T> {

    private String columnKey;

    private Integer size;

    public AbstractDomainColumn() {
        super(Model.of(""), null);
    }

    public AbstractDomainColumn(IModel<String> displayModel, Sort sort) {
        super(displayModel, sort);
    }

    public AbstractDomainColumn(EntityAttribute entityAttribute){
        super(displayModel(entityAttribute), sortProperty(entityAttribute));
    }

    public AbstractDomainColumn(String columnKey) {
        super(new ResourceModel(columnKey), new Sort(columnKey));

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

    protected static Sort sortProperty(EntityAttribute entityAttribute){
        if (entityAttribute != null){
            return new Sort(entityAttribute.getValueType().getKey(), entityAttribute);
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
