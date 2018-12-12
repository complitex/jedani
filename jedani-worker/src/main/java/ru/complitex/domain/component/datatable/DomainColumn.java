package ru.complitex.domain.component.datatable;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.wicket.datatable.FilterDataForm;
import ru.complitex.common.wicket.datatable.TextDataFilter;
import ru.complitex.common.wicket.panel.InputPanel;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.entity.Value;
import ru.complitex.domain.model.DateAttributeModel;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.model.TextAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Attributes;

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

    private EntityService entityService;

    private DomainService domainService;

    public DomainColumn(EntityAttribute entityAttribute, EntityService entityService, DomainService domainService) {
        super(entityAttribute);

        this.entityAttribute = entityAttribute;

        this.entityService = entityService;
        this.domainService = domainService;
    }

    @Override
    public Component getFilter(String componentId, FilterDataForm<?> form) {
        Long entityAttributeId = entityAttribute.getEntityAttributeId();

        Domain domain = (Domain)((FilterWrapper)form.getModelObject()).getObject();

        domain.getOrCreateAttribute(entityAttributeId).setEntityAttribute(entityAttribute);

        switch (entityAttribute.getValueType()){
            case NUMBER:
                TextDataFilter<Long> textFilter = new TextDataFilter<>(componentId, new NumberAttributeModel(domain, entityAttributeId), form);
                textFilter.getFilter().setType(Long.class);

                return textFilter;
            case DATE:
                return new InputPanel(componentId, new DateTextField(InputPanel.INPUT_COMPONENT_ID,
                        new DateAttributeModel(domain, entityAttributeId),
                        new DateTextFieldConfig().withFormat("dd.MM.yyyy").withLanguage("ru")));
            default:
                return new TextDataFilter<>(componentId, new TextAttributeModel(domain, entityAttributeId, TextAttributeModel.TYPE.DEFAULT), form);
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
                            .collect(Collectors.joining(", "));
                }

                break;
            case ENTITY:
                if (attribute.getNumber() != null) {
                    Domain domain = domainService.getDomain(entityService
                            .getEntity(entityAttribute.getReferenceId()).getName(), attribute.getNumber());

                    text = domain != null && entityAttribute.getReferenceEntityAttribute() != null
                            ? domain.getValueText(entityAttribute.getReferenceEntityAttribute().getEntityAttributeId())
                            : attribute.getNumber() + "";

                    text = Attributes.displayText(entityAttribute.getReferenceEntityAttribute(), text);
                }

                break;
            case NUMBER:
                text = attribute.getNumber() != null ?  attribute.getNumber() + "" : "";

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
                                    Domain domain = domainService.getDomain(referenceEntityAttribute.getEntityName(), id);

                                    String prefix = "";

                                    if (prefixEntityAttribute != null){
                                        Long prefixDomainId = domain.getNumber(prefixEntityAttribute.getEntityAttributeId());

                                        if (prefixDomainId != null) {
                                            Domain prefixDomain = domainService.getDomain(prefixEntityAttribute
                                                            .getReferenceEntityAttribute().getEntityName(),
                                                    prefixDomainId);

                                            prefix = prefixDomain.getValueText(prefixEntityAttribute.getReferenceEntityAttribute().getEntityAttributeId());

                                            prefix = prefix != null ? prefix + " " : "";
                                        }
                                    }

                                    String valueText = domain.getValueText(referenceEntityAttribute.getEntityAttributeId());

                                    return prefix.toLowerCase() + Attributes.displayText(referenceEntityAttribute, valueText);
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

        Label label = new Label(componentId, text);

        if (entityAttribute.getPrefixEntityAttribute() != null){
            label.add(AttributeAppender.append("style", "white-space: nowrap"));
        }

        cellItem.add(label);
    }
}
