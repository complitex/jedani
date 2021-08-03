package ru.complitex.domain.component.datatable;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.cdi.NonContextual;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.wicket.table.DateFilter;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.common.wicket.table.TextFilter;
import ru.complitex.domain.entity.*;
import ru.complitex.domain.model.DateAttributeModel;
import ru.complitex.domain.model.DecimalAttributeModel;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.model.TextAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Attributes;

import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 19.12.2017 7:55
 */
public class DomainColumn<T extends Domain> extends AbstractDomainColumn<T> implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(DomainColumn.class);

    @Inject
    private transient EntityService entityService;

    @Inject
    private transient DomainService domainService;

    private final EntityAttribute entityAttribute;

    private boolean loadReference = true;

    public DomainColumn(EntityAttribute entityAttribute) {
        super(entityAttribute);

        this.entityAttribute = entityAttribute;
    }

    public DomainColumn(EntityAttribute entityAttribute, IModel<String> displayModel) {
        super(displayModel != null ? displayModel : displayModel(entityAttribute), sortProperty(entityAttribute));

        this.entityAttribute = entityAttribute;
    }

    public EntityService getEntityService() {
        if (entityService == null){
            NonContextual.of(this).inject(this);
        }

        return entityService;
    }

    public DomainService getDomainService() {
        if (domainService == null){
            NonContextual.of(this).inject(this);
        }

        return domainService;
    }

    @Override
    public Component newFilter(String componentId, Table<T> table) {
        Long entityAttributeId = entityAttribute.getEntityAttributeId();

        T domain = table.getFilterWrapper().getObject();

        Attribute attribute = domain.getOrCreateAttribute(entityAttributeId);

        attribute.setEntityAttribute(entityAttribute);

        switch (entityAttribute.getValueType()){
            case NUMBER:
                return new TextFilter<>(componentId, new NumberAttributeModel(domain, entityAttributeId))
                        .setType(Long.class)
                        .onChange(table::update);
            case DECIMAL:
                return new TextFilter<>(componentId, new DecimalAttributeModel(domain, entityAttributeId))
                        .setType(BigDecimal.class)
                        .onChange(table::update);
            case DATE:
                attribute.setFilter(Attribute.FILTER_SAME_DAY);

                return new DateFilter(componentId, new DateAttributeModel(domain, entityAttributeId))
                        .onChange(table::update);
            default:
                return new TextFilter<>(componentId, new TextAttributeModel(domain, entityAttributeId, StringType.DEFAULT))
                        .onChange(table::update);
        }
    }

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        if (loadReference){
            getEntityService().loadReference(entityAttribute);

            loadReference = false;
        }

        String text = "";

        Attribute attribute = rowModel.getObject().getOrCreateAttribute(entityAttribute.getEntityAttributeId());

        switch (entityAttribute.getValueType()){
            case TEXT_LIST:
                List<Value> values = attribute.getValues();

                if (values != null && !values.isEmpty()) {
                    if (values.get(0).getLocaleId() != null){
                        text = Attributes.displayText(entityAttribute,
                                attribute.getTextValue(entityAttribute.getEntityAttributeId()));
                    }else{
                        text = values.stream()
                                .filter(v -> v.getLocaleId() == null)
                                .map(v -> Attributes.displayText(entityAttribute, v.getText()))
                                .collect(Collectors.joining("\n"));
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
            case DECIMAL:
                text = attribute.getDecimal() != null ?  attribute.getDecimal() + "" : "";

                break;

            case DATE:
                text = attribute.getDate() != null ? dateFormat.format(attribute.getDate()) : "";

                break;

            case ENTITY_LIST:
                if (attribute.getValues() != null && entityAttribute.hasReferenceEntityAttributes()){
                    EntityAttribute referenceEntityAttribute = entityAttribute.getReferenceEntityAttributes().get(0);

                    if (referenceEntityAttribute != null){
                        List<Long> list = attribute.getValues().stream()
                                .map(Value::getNumber)
                                .collect(Collectors.toList());

                        EntityAttribute prefixEntityAttribute = entityAttribute.getPrefixEntityAttribute();

                        text = list.stream()
                                .map(id -> {
                                    Domain domain = getDomainService().getDomain(referenceEntityAttribute.getEntityName(), id);

                                    String prefix = "";

                                    if (prefixEntityAttribute != null){
                                        Long prefixDomainId = domain.getNumber(prefixEntityAttribute.getEntityAttributeId());

                                        if (prefixDomainId != null && prefixEntityAttribute.hasReferenceEntityAttributes()) {
                                            EntityAttribute ea = prefixEntityAttribute.getReferenceEntityAttributes().get(0);

                                            Domain prefixDomain = getDomainService().getDomain(ea.getEntityName(), prefixDomainId);

                                            prefix = prefixDomain.getTextValue(ea.getEntityAttributeId());

                                            prefix = prefix != null ? prefix + " " : "";
                                        }
                                    }

                                    String valueText = domain.getTextValue(referenceEntityAttribute.getEntityAttributeId());

                                    return prefix.toLowerCase() + Attributes.displayText(referenceEntityAttribute, valueText);
                                })
                                .collect(Collectors.joining("\n"));
                    }else{
                        List<String> list = attribute.getValues().stream()
                                .map(v -> Attributes.displayText(entityAttribute, v.getText()))
                                .collect(Collectors.toList());

                        text = String.join("\n", list);
                    }
                }

                break;
            default:
                text = Attributes.displayText(entityAttribute, attribute.getText());
        }

        MultiLineLabel label = new MultiLineLabel(componentId, text);

        if (entityAttribute.getPrefixEntityAttribute() != null){
            label.add(AttributeAppender.append("style", "white-space: nowrap"));
        }

        cellItem.add(label);
    }

    protected String displayEntity(EntityAttribute entityAttribute, Long objectId){
        if (entityAttribute.getReferenceId() != null) {
            Domain refDomain = getDomainService().getDomainRef(entityAttribute.getReferenceId(), objectId);

            if (refDomain != null){
                if (entityAttribute.hasReferenceEntityAttributes()) {
                    List<String> list = new ArrayList<>();

                    for (EntityAttribute ea : entityAttribute.getReferenceEntityAttributes()){
                        String text;

                        switch (ea.getValueType()){
                            case ENTITY:
                                text = displayEntity(ea, refDomain.getNumber(ea.getEntityAttributeId()));
                                break;
                            case TEXT_LIST:
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
