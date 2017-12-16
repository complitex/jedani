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
    private Long id;
    private Long objectId;
    private Long entityAttributeId;
    private Long valueId;
    private Date startDate;
    private Date endDate;
    private Status status;

    private String entityName;

    private List<Value> values;

    public Attribute() {
    }

    public Attribute(Long entityAttributeId) {
        this.entityAttributeId = entityAttributeId;
    }

    public Value getValue(Long localeId){
        if (values != null){
            for (Value sc: values){
                if (sc.getLocaleId().equals(localeId)){
                    return sc;
                }
            }
        }

        return null;
    }

    public String getText(){
        Value value = getValue(Locales.getSystemLocaleId());

        return value != null ? value.getText() : null;
    }

    public String getText(java.util.Locale locale){
        Value value = getValue(Locales.getLocaleId(locale));

        return value != null ? value.getText() : null;
    }

    public void setText(String value, long localeId){
        values.stream().filter(s -> s.getLocaleId().equals(localeId) ||
                (Locales.getSystemLocaleId().equals(s.getLocaleId()) && s.getText() == null))
                .forEach(s -> s.setText(value));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Value> getValues() {
        return values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }
}
