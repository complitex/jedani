package ru.complitex.domain.component.datatable;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
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
import ru.complitex.common.wicket.panel.InputPanel;
import ru.complitex.domain.component.form.DomainAutoComplete;
import ru.complitex.domain.entity.*;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.mapper.EntityMapper;
import ru.complitex.domain.model.DateAttributeModel;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.model.TextAttributeModel;
import ru.complitex.domain.util.Attributes;
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
        Long entityAttributeId = entityAttribute.getEntityAttributeId();

        Domain domain = (Domain)((FilterWrapper)form.getDefaultModelObject()).getObject();

        switch (entityAttribute.getValueType()){
            case NUMBER:
                return new TextFilter<>(componentId, new NumberAttributeModel(domain, entityAttributeId), form);
            case DATE:
                return new InputPanel(componentId, new DateTextField(InputPanel.INPUT_COMPONENT_ID,
                        new DateAttributeModel(domain, entityAttributeId),
                        new DateTextFieldConfig().withFormat("dd.MM.yyyy").withLanguage("ru")));
            case ENTITY:
            case ENTITY_VALUE:
                if (entityAttribute.getReferenceEntityAttribute() != null) {
                    Entity entity = getEntityMapper().getEntity(entityAttribute.getReferenceId());

                    return new DomainAutoComplete(componentId, entity.getName(),
                            entityAttribute.getReferenceEntityAttribute().getEntityAttributeId(),
                            new NumberAttributeModel(domain, entityAttributeId), entityAttribute.isDisplayCapitalize()){
                        @Override
                        protected String getPrefix(Attribute attribute) {
                            String prefix = "";

                            EntityAttribute prefixEntityAttribute = entityAttribute.getPrefixEntityAttribute();

                            if (prefixEntityAttribute != null && attribute.getObjectId() != null){
                                Domain domain = getDomainMapper().getDomain(entity.getName(), attribute.getObjectId());

                                Long prefixDomainId = domain.getNumber(prefixEntityAttribute.getEntityAttributeId());

                                if (prefixDomainId != null) {
                                    Domain prefixDomain = getDomainMapper().getDomain(prefixEntityAttribute
                                                    .getReferenceEntityAttribute().getEntityName(),
                                            prefixDomainId);

                                    prefix = prefixDomain.getValueText(prefixEntityAttribute.getReferenceEntityAttribute().getEntityAttributeId());

                                    return prefix != null ? prefix + " " : "";
                                }
                            }

                            return "";
                        }
                    };
                }
            default:
                return new TextFilter<>(componentId, new TextAttributeModel(domain, entityAttributeId, TextAttributeModel.TYPE.DEFAULT), form);
        }
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        String text = "";

        Attribute attribute = rowModel.getObject().getOrCreateAttribute(entityAttribute.getEntityAttributeId());

        switch (entityAttribute.getValueType()){
            case TEXT_VALUE:
                if (attribute.getValues() != null) {
                    text = attribute.getValues().stream().map(v -> Attributes.displayText(entityAttribute, v.getText()))
                            .collect(Collectors.joining(","));
                }

                break;
            case ENTITY:
                if (attribute.getNumber() != null) {
                    Domain domain = getDomainMapper().getDomain(getEntityMapper()
                            .getEntity(entityAttribute.getReferenceId()).getName(), attribute.getNumber());

                    text = domain != null
                            ? domain.getAttributes().get(0).getValue(Locales.getSystemLocaleId()).getText()
                            : attribute.getNumber() + ""; //todo def attribute

                    text = Attributes.displayText(entityAttribute, text);
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
                    EntityAttribute referenceEntityAttribute = entityAttribute.getReferenceEntityAttribute();

                    if (referenceEntityAttribute != null){
                        List<Long> list = attribute.getValues().stream()
                                .map(Value::getNumber)
                                .collect(Collectors.toList());

                        EntityAttribute prefixEntityAttribute = entityAttribute.getPrefixEntityAttribute();

                        text = list.stream()
                                .map(id -> {
                                    Domain domain = getDomainMapper().getDomain(referenceEntityAttribute.getEntityName(), id);

                                    String prefix = "";

                                    if (prefixEntityAttribute != null){
                                        Long prefixDomainId = domain.getNumber(prefixEntityAttribute.getEntityAttributeId());

                                        if (prefixDomainId != null) {
                                            Domain prefixDomain = getDomainMapper().getDomain(prefixEntityAttribute
                                                            .getReferenceEntityAttribute().getEntityName(),
                                                    prefixDomainId);

                                            prefix = prefixDomain.getValueText(prefixEntityAttribute.getReferenceEntityAttribute().getEntityAttributeId());

                                            prefix = prefix != null ? prefix + " " : "";
                                        }
                                    }

                                    String valueText = domain.getValueText(referenceEntityAttribute.getEntityAttributeId());

                                    return prefix.toLowerCase() + Attributes.displayText(entityAttribute, valueText);
                                })
                                .collect(Collectors.joining(", "));
                    }else{
                        List<String> list = attribute.getValues().stream()
                                .map(v -> Attributes.displayText(entityAttribute, v.getText()))
                                .collect(Collectors.toList());

                        text = String.join(", ", list);
                    }
                }

                break;
            default:
                text = Attributes.displayText(entityAttribute, attribute.getText());
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
