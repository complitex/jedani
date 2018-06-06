package ru.complitex.domain.component.datatable;

import org.apache.wicket.Component;
import org.apache.wicket.cdi.NonContextual;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.domain.component.form.DomainAutoComplete;
import ru.complitex.domain.entity.*;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.mapper.EntityMapper;
import ru.complitex.domain.util.Locales;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 19.12.2017 7:55
 */
public class DomainColumn<T extends Domain> extends AbstractDomainColumn<T> {
    private static Logger log = LoggerFactory.getLogger(DomainColumn.class);

    private EntityAttribute entityAttribute;

    private EntityMapper entityMapper;

    private DomainMapper domainMapper;

    public DomainColumn(EntityAttribute entityAttribute) {
        super(Model.of(entityAttribute.getValue() != null
                        ? entityAttribute.getValue().getText()
                        : "[" + entityAttribute.getEntityAttributeId() + "]"),
                new SortProperty(entityAttribute.getValueType().getKey(), entityAttribute));

        this.entityAttribute = entityAttribute;
    }

    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        IModel<?> model;

        switch (entityAttribute.getValueType()){
            case NUMBER:
                model = new IModel<Long>() {
                    @Override
                    public Long getObject() {
                        return ((Domain)((FilterWrapper)form.getDefaultModelObject()).getObject())
                                .getNumber(entityAttribute.getEntityAttributeId());
                    }

                    @Override
                    public void setObject(Long object) {
                        ((Domain)((FilterWrapper)form.getDefaultModelObject()).getObject())
                                .setNumber(entityAttribute.getEntityAttributeId(), object);
                    }
                };

                return new TextFilter<>(componentId, model, form);
            case ENTITY:
            case ENTITY_VALUE:
                if (entityAttribute.getRefEntityAttribute() != null) {
                    Entity entity = getEntityMapper().getEntity(entityAttribute.getReferenceId());

                    return new DomainAutoComplete(componentId, entity.getName(),
                            entityAttribute.getRefEntityAttribute().getEntityAttributeId(),
                            new IModel<Long>() {
                                @Override
                                public Long getObject() {
                                    return ((Domain)((FilterWrapper)form.getDefaultModelObject()).getObject())
                                            .getNumber(entityAttribute.getEntityAttributeId());
                                }

                                @Override
                                public void setObject(Long object) {
                                    ((Domain)((FilterWrapper)form.getDefaultModelObject()).getObject())
                                            .setNumber(entityAttribute.getEntityAttributeId(), object);
                                }
                            });
                }
            default:
                model = new IModel<String>() {
                    @Override
                    public String getObject() {
                        return ((Domain)((FilterWrapper)form.getDefaultModelObject()).getObject())
                                .getText(entityAttribute.getEntityAttributeId());
                    }

                    @Override
                    public void setObject(String object) {
                        ((Domain)((FilterWrapper)form.getDefaultModelObject()).getObject())
                                .setText(entityAttribute.getEntityAttributeId(), object);
                    }
                };
                return new TextFilter<>(componentId, model, form);
        }
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        String text = "";

        Attribute attribute = rowModel.getObject().getOrCreateAttribute(entityAttribute.getEntityAttributeId());

        switch (entityAttribute.getValueType()){
            case TEXT_VALUE:
                Value value = attribute.getValue(Locales.getSystemLocaleId());
                text = value != null ? value.getText() : null;

                break;
            case ENTITY:
                if (attribute.getNumber() != null) {
                    Domain domain = getDomainMapper().getDomain(getEntityMapper()
                            .getEntity(entityAttribute.getReferenceId()).getName(), attribute.getNumber());

                    text = domain != null
                            ? domain.getAttributes().get(0).getValue(Locales.getSystemLocaleId()).getText()
                            : attribute.getNumber() + ""; //todo def attribute
                }

                break;
            case NUMBER:
                text = attribute.getNumber() + "";

                break;

            case DATE:
                text = attribute.getDate() != null ? dateFormat.format(attribute.getDate()) : "";

                break;

            case ENTITY_VALUE:
                if (attribute.getValues() != null){
                    EntityAttribute refEA = entityAttribute.getRefEntityAttribute();

                    if (refEA != null){
                        List<Long> list = attribute.getValues().stream()
                                .map(Value::getNumber)
                                .collect(Collectors.toList());

                        text = list.stream()
                                .map(id -> getDomainMapper().getDomain(refEA.getEntityName(), id)
                                        .getValueText(refEA.getEntityAttributeId()))
                                .collect(Collectors.joining(", "));
                    }else{
                        List<String> list = attribute.getValues().stream()
                                .map(Value::getText)
                                .collect(Collectors.toList());

                        text = String.join(", ", list);
                    }
                }

                break;
            default:
                text = attribute.getText();
        }

        cellItem.add(new Label(componentId, text));
    }

    private EntityMapper getEntityMapper(){
        if (entityMapper == null){
            entityMapper = new EntityMapper();

            NonContextual.of(EntityMapper.class).inject(entityMapper);
        }

        return entityMapper;
    }

    private DomainMapper getDomainMapper(){
        if (domainMapper == null){
            domainMapper = new DomainMapper();

            NonContextual.of(DomainMapper.class).inject(domainMapper);
        }

        return domainMapper;
    }
}
