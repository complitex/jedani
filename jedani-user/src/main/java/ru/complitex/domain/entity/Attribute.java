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
    private String text;
    private Long number;
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
            return values.stream().filter(sc -> sc.getLocaleId().equals(localeId))
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }


    public Value getValue(java.util.Locale locale){
        return getValue(Locales.getLocaleId(locale));
    }

    public void setValue(String text, Long localeId){
        if (values == null){
            values = Value.newValues();
        }

        values.stream().filter(value -> value.getLocaleId().equals(localeId))
                .findFirst()
                .ifPresent(value -> value.setText(text));
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
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
