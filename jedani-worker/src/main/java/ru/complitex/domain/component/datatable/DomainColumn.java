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
import ru.complitex.domain.entity.*;
import ru.complitex.domain.model.DateAttributeModel;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.model.TextAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Attributes;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 19.12.2017 7:55
 */
public class DomainColumn<T extends Domain> extends AbstractDomainColumn<T> {
    private static Logger log = LoggerFactory.getLogger(DomainColumn.class);

    @Inject
    private EntityService entityService;

    @Inject
    private DomainService domainService;

    private EntityAttribute entityAttribute;

    private boolean loadReference = true;

    public DomainColumn(EntityAttribute entityAttribute) {
        super(entityAttribute);

        this.entityAttribute = entityAttribute;
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
                        new DateTextFieldConfig().withFormat("dd.MM.yyyy").withLanguage("ru").autoClose(true)));
            default:
                return new TextDataFilter<>(componentId, new TextAttributeModel(domain, entityAttributeId, StringType.DEFAULT), form);
        }
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        if (loadReference){
            entityService.loadReference(entityAttribute);

            loadReference = false;
        }

        String text = "";

        Attribute attribute = rowModel.getObject().getOrCreateAttribute(entityAttribute.getEntityAttributeId());

        switch (entityAttribute.getValueType()){
            case TEXT_VALUE:
                List<Value> values = attribute.getValues();

                if (values != null && !values.isEmpty()) {
                    if (values.get(0).getLocaleId() != null){
                        text = Attributes.displayText(entityAttribute,
                                attribute.getTextValue(entityAttribute.getEntityAttributeId()));
                    }else{
                        text = values.stream()
                                .filter(v -> v.getLocaleId() == null)
                                .map(v -> Attributes.displayText(entityAttribute, v.getText()))
                                .collect(Collectors.joining(", "));
                    }
                }

                break;
            case ENTITY:
                if (attribute.getNumber() != null) {
                    text = displayEntity(entityAttribute, attribute.getNumber());
                }

                break;
            case NUMBER:
                text = attribute.getNumber() != null ?  attribute.getNumber() + "" : "";

                break;

            case DATE:
                text = attribute.getDate() != null ? dateFormat.format(attribute.getDate()) : "";

                break;

            case ENTITY_VALUE:
                if (attribute.getValues() != null && entityAttribute.hasReferenceEntityAttributes()){
                    EntityAttribute referenceEntityAttribute = entityAttribute.getReferenceEntityAttributes().get(0);

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

                                        if (prefixDomainId != null && prefixEntityAttribute.hasReferenceEntityAttributes()) {
                                            EntityAttribute ea = prefixEntityAttribute.getReferenceEntityAttributes().get(0);

                                            Domain prefixDomain = domainService.getDomain(ea.getEntityName(), prefixDomainId);

                                            prefix = prefixDomain.getTextValue(ea.getEntityAttributeId());

                                            prefix = prefix != null ? prefix + " " : "";
                                        }
                                    }

                                    String valueText = domain.getTextValue(referenceEntityAttribute.getEntityAttributeId());

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

    protected String displayEntity(EntityAttribute entityAttribute, Long objectId){
        if (entityAttribute.getReferenceId() != null) {
            Domain refDomain = domainService.getDomainRef(entityAttribute.getReferenceId(), objectId);

            if (refDomain != null){
                if (entityAttribute.hasReferenceEntityAttributes()) {
                    List<String> list = new ArrayList<>();

                    for (EntityAttribute ea : entityAttribute.getReferenceEntityAttributes()){
                        String text;

                        switch (ea.getValueType()){
                            case ENTITY:
                                text = displayEntity(ea, refDomain.getNumber(ea.getEntityAttributeId()));
                                break;
                            case TEXT_VALUE:
                                text = refDomain.getTextValue(ea.getEntityAttributeId());
                                break;
                            case TEXT:
                                text = refDomain.getText(ea.getEntityAttributeId());
                                break;
                            case NUMBER:
                                text = refDomain.getNumber(ea.getEntityAttributeId()) + "";
                                break;
                            default:
                                text = "[" + ea.getEntityAttributeId() + "]";
                        }

                        list.add(Attributes.displayText(ea, text));
                    }

                    return String.join(", ", list);
                }
            }
        }

        return entityAttribute.getEntityAttributeId() + ":" + objectId;
    }
}
