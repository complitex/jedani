package ru.complitex.domain.component;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.entity.Value;
import ru.complitex.domain.util.Locales;

/**
 * @author Anatoly A. Ivanov
 * 19.12.2017 7:55
 */
public class DomainColumn extends AbstractDomainColumn {
    private EntityAttribute entityAttribute;

    public DomainColumn(EntityAttribute entityAttribute) {
        super(Model.of(entityAttribute.getValue().getText()));

        this.entityAttribute = entityAttribute;
    }

    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        IModel<?> model;

        switch (entityAttribute.getValueType()){
            case INTEGER:
                model = new IModel<Long>() {
                    @Override
                    public Long getObject() {
                        return ((Domain)((FilterWrapper)form.getDefaultModelObject()).getObject())
                                .getNumber(entityAttribute.getAttributeId());
                    }

                    @Override
                    public void setObject(Long object) {
                        ((Domain)((FilterWrapper)form.getDefaultModelObject()).getObject())
                                .setNumber(entityAttribute.getAttributeId(), object);
                    }
                };

                break;
            default:
                model = new IModel<String>() {
                    @Override
                    public String getObject() {
                        return ((Domain)((FilterWrapper)form.getDefaultModelObject()).getObject())
                                .getText(entityAttribute.getAttributeId());
                    }

                    @Override
                    public void setObject(String object) {
                        ((Domain)((FilterWrapper)form.getDefaultModelObject()).getObject())
                                .setText(entityAttribute.getAttributeId(), object);
                    }
                };
        }

        return new TextFilter<>(componentId, model, form);
    }

    @Override
    public void populateItem(Item<ICellPopulator<Domain>> cellItem, String componentId, IModel<Domain> rowModel) {
        IModel model;

        switch (entityAttribute.getValueType()){
            case VALUE:
                Value value = rowModel.getObject().getValue(entityAttribute.getAttributeId(), Locales.RU);
                model = Model.of(value != null ? value.getText() : null);

                break;
            case INTEGER:
                model = Model.of(rowModel.getObject().getNumber(entityAttribute.getAttributeId()));

                break;
            default:
                model = Model.of(rowModel.getObject().getText(entityAttribute.getAttributeId()));
        }

        cellItem.add(new Label(componentId, model));
    }
}
