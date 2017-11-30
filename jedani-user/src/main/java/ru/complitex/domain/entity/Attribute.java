package ru.complitex.domain.entity;

import ru.complitex.domain.util.Locales;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 15:29
 */
public class Attribute implements Serializable{
    private Long pkId;
    private Long attributeId;
    private Long objectId;
    private Long entityAttributeId;
    private Long valueId;
    private Date startDate;
    private Date endDate;
    private Status status;

    private String entityName;

    private List<StringValue> stringValues;

    public Attribute() {
    }

    public Attribute(Long entityAttributeId, Long attributeId) {
        this.entityAttributeId = entityAttributeId;
        this.attributeId = attributeId;
    }

    public StringValue getStringValue(Long localeId){
        if (stringValues != null){
            for (StringValue sc: stringValues){
                if (sc.getLocaleId().equals(localeId)){
                    return sc;
                }
            }
        }

        return null;
    }

    public String getStringValue(){
        StringValue stringValue = getStringValue(Locales.getSystemLocaleId());

        return stringValue != null ? stringValue.getValue() : null;
    }

    public String getStringValue(java.util.Locale locale){
        StringValue stringValue = getStringValue(Locales.getLocaleId(locale));

        return stringValue != null ? stringValue.getValue() : null;
    }

    public void setStringValue(String value, long localeId){
        if (stringValues == null){
            stringValues = StringValue.newStringValues();
        }

        stringValues.stream()
                .filter(s -> s.getLocaleId().equals(localeId) ||
                        (Locales.getSystemLocaleId().equals(s.getLocaleId()) && s.getValue() == null))
                .forEach(s -> s.setValue(value));
    }

    public Long getPkId() {
        return pkId;
    }

    public void setPkId(Long pkId) {
        this.pkId = pkId;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public Long getEntityAttributeId() {
        return entityAttributeId;
    }

    public void setEntityAttributeId(Long entityAttributeId) {
        this.entityAttributeId = entityAttributeId;
    }

    public Long getValueId() {
        return valueId;
    }

    public void setValueId(Long valueId) {
        this.valueId = valueId;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public List<StringValue> getStringValues() {
        return stringValues;
    }

    public void setStringValues(List<StringValue> stringValues) {
        this.stringValues = stringValues;
    }
}
