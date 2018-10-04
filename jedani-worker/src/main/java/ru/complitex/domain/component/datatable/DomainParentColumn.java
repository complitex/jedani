package ru.complitex.domain.component.datatable;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.entity.Value;
import ru.complitex.domain.util.Attributes;
import ru.complitex.domain.util.Locales;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 13:12
 */
public abstract class DomainParentColumn<T extends Domain> extends AbstractDomainColumn<T>{
    private Entity entity;
    private Long entityAttributeId;

    public DomainParentColumn(Entity entity, Long entityAttributeId) {
        super(Model.of(entity.getValue().getText()), new SortProperty("parentEntityId", entity.getId()));

        this.entity = entity;
        this.entityAttributeId = entityAttributeId;
    }

    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        EntityAttribute parentEntityAttribute = new EntityAttribute();
        parentEntityAttribute.setEntityName(entity.getName());
        parentEntityAttribute.setEntityAttributeId(entityAttributeId);

        Domain domain = (Domain) ((FilterWrapper)form.getDefaultModelObject()).getObject();
        domain.setParentEntityAttribute(parentEntityAttribute);

        return new TextFilter<String>(componentId, new PropertyModel<>(form.getModel(), "map.parentName"), form);
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        IModel model = Model.of("");

        Domain domain = getDomain(rowModel.getObject().getParentId());

        if (domain != null) {
            switch (entity.getEntityAttribute(entityAttributeId).getValueType()){
                case TEXT_VALUE:
                    Value value = domain.getValue(entityAttributeId, Locales.RU);
                    model = Model.of(value != null ? Attributes.capitalize(value.getText()) : null);

                    break;
                case ENTITY:
                case NUMBER:
                    model = Model.of(domain.getNumber(entityAttributeId));

                    break;
                default:
                    model = Model.of(Attributes.capitalize(domain.getText(entityAttributeId)));
            }
        }

        cellItem.add(new Label(componentId, model));

    }

    protected abstract Domain getDomain(Long objectId);
}
