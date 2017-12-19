package ru.complitex.domain.component;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 20.12.2017 2:17
 */
public class DomainIdColumn extends AbstractDomainColumn{
    public DomainIdColumn() {
        super(Model.of("#"));
    }

    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        return new TextFilter<>(componentId, new PropertyModel<>(form.getDefaultModel(), "object.objectId"), form);
    }

    @Override
    public void populateItem(Item<ICellPopulator<Domain>> cellItem, String componentId, IModel<Domain> rowModel) {
        cellItem.add(new Label(componentId, () -> rowModel.getObject().getObjectId()));
    }

    @Override
    public String getCssClass() {
        return "domain-id-column";
    }
}
