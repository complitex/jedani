package ru.complitex.domain.component.datatable;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import ru.complitex.common.entity.Sort;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.common.wicket.table.TextFilter;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.entity.Value;
import ru.complitex.domain.util.Attributes;
import ru.complitex.domain.util.Locales;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 13:12
 */
public abstract class DomainParentColumn<T extends Domain<T>> extends AbstractDomainColumn<T>{
    private final EntityAttribute entityAttribute;

    public DomainParentColumn(IModel<String> displayModel, EntityAttribute entityAttribute) {
        super(displayModel, new Sort("parent", entityAttribute));

        this.entityAttribute = entityAttribute;
    }

    @Override
    public Component getHeader(String componentId, Table<T> table) {
        Domain<?> domain = table.getFilterWrapper().getObject();

        domain.setParentEntityAttribute(entityAttribute);

        return new TextFilter<>(componentId, new PropertyModel<>(table.getFilterWrapper(), "map.parentName"));
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        IModel<?> model = Model.of("");

        Domain<?> domain = getDomain(rowModel.getObject().getParentId());

        if (domain != null) {
            switch (entityAttribute.getValueType()){
                case TEXT_LIST:
                    Value value = domain.getValue(entityAttribute.getEntityAttributeId(), Locales.RU);
                    model = Model.of(value != null ? Attributes.capitalize(value.getText()) : null);

                    break;
                case ENTITY:
                case NUMBER:
                    model = Model.of(domain.getNumber(entityAttribute.getEntityAttributeId()));

                    break;
                default:
                    model = Model.of(Attributes.capitalize(domain.getText(entityAttribute.getEntityAttributeId())));
            }
        }

        cellItem.add(new Label(componentId, model));

    }

    protected abstract Domain<?> getDomain(Long objectId);
}
