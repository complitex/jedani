package ru.complitex.domain.component.datatable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 19.12.2017 7:55
 */
public class DomainColumn extends AbstractDomainColumn {
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
            case ENTITY: //todo test reset filter
                Entity entity = getEntityMapper().getEntity(entityAttribute.getReferenceId());

                return new DomainAutoComplete(componentId, entity.getName(),
                        1L,
                        new IModel<Long>() { //todo number model
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

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void populateItem(Item<ICellPopulator<Domain>> cellItem, String componentId, IModel<Domain> rowModel) {
        String text = "";

        Attribute attribute = rowModel.getObject().getOrCreateAttribute(entityAttribute.getEntityAttributeId());

        switch (entityAttribute.getValueType()){
            case VALUE:
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

            case JSON:
                if (attribute.getJson() != null){
                    EntityAttribute refEA = entityAttribute.getRefEntityAttribute();

                    if (refEA != null){
                        try {
                            List<Long> list = objectMapper.readValue(attribute.getJson(), new TypeReference<List<Long>>(){});

                            text = list.stream()
                                    .map(id -> getDomainMapper().getDomain(refEA.getEntityName(), id)
                                            .getValueText(refEA.getEntityAttributeId()))
                                    .collect(Collectors.joining(", "));
                        } catch (IOException e) {
                            log.error("error parse json ", e);

                            throw new WicketRuntimeException(e);
                        }
                    }else{
                        try {
                            List<String> list = objectMapper.readValue(attribute.getJson(), new TypeReference<List<String>>(){});

                            text = String.join(", ", list);
                        } catch (IOException e) {
                            log.error("error parse json ", e);

                            throw new WicketRuntimeException(e);
                        }
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
