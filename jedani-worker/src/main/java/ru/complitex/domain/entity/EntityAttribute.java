package ru.complitex.domain.entity;

import ru.complitex.domain.util.Locales;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 06.12.2017 17:12
 */
public class EntityAttribute implements Serializable{
    private Long id;
    private Long entityId;
    private Long entityAttributeId;
    private Date startDate;
    private Date endDate;
    private ValueType valueType;
    private Long referenceId;

    private List<EntityValue> values;

    private String entityName;

    private EntityAttribute referenceEntityAttribute;

    private String referenceEntityName;
    private Long referenceEntityAttributeId;

    private EntityAttribute prefixEntityAttribute;

    private boolean displayLowerCase;

    public EntityValue getValue(){
        return values.stream().filter(v -> v.getLocaleId().equals(Locales.getSystemLocaleId())).findAny().orElse(null);
    }

    public String getValueText(){
        EntityValue entityValue = getValue();

        if (entityValue != null){
            return entityValue.getText();
        }

        return "[" + entityAttributeId + "]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getEntityAttributeId() {
        return entityAttributeId;
    }

    public void setEntityAttributeId(Long entityAttributeId) {
        this.entityAttributeId = entityAttributeId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public EntityAttribute setValueType(ValueType valueType) {
        this.valueType = valueType;

        return this;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public List<EntityValue> getValues() {
        return values;
    }

    public void setValues(List<EntityValue> values) {
        this.values = values;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public EntityAttribute getReferenceEntityAttribute() {
        return referenceEntityAttribute;
    }

    public EntityAttribute setReferenceEntityAttribute(EntityAttribute referenceEntityAttribute) {
        this.referenceEntityAttribute = referenceEntityAttribute;

        return this;
    }

    public EntityAttribute getPrefixEntityAttribute() {
        return prefixEntityAttribute;
    }

    public EntityAttribute setPrefixEntityAttribute(EntityAttribute prefixEntityAttribute) {
        this.prefixEntityAttribute = prefixEntityAttribute;

        return this;
    }

    public boolean isDisplayLowerCase() {
        return displayLowerCase;
    }

    public EntityAttribute setDisplayLowerCase(boolean displayLowerCase) {
        this.displayLowerCase = displayLowerCase;

        return this;
    }

    public String getReferenceEntityName() {
        return referenceEntityName;
    }

    public void setReferenceEntityName(String referenceEntityName) {
        this.referenceEntityName = referenceEntityName;
    }

    public Long getReferenceEntityAttributeId() {
        return referenceEntityAttributeId;
    }

    public void setReferenceEntityAttributeId(Long referenceEntityAttributeId) {
        this.referenceEntityAttributeId = referenceEntityAttributeId;
    }

    public EntityAttribute withReference(String referenceEntityName, Long referenceEntityAttributeId){
        this.referenceEntityName = referenceEntityName;
        this.referenceEntityAttributeId = referenceEntityAttributeId;

        return this;
    }
}
