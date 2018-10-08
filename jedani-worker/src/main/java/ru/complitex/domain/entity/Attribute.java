package ru.complitex.domain.entity;

import ru.complitex.domain.util.Locales;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
    private Date date;
    private Date startDate;
    private Date endDate;
    private Status status;
    private Long userId;

    private String entityName;

    private List<Value> values = new ArrayList<>();

    private EntityAttribute entityAttribute;

    public Attribute() {
    }

    public Attribute(Long entityAttributeId) {
        this.entityAttributeId = entityAttributeId;
    }

    public Value getValue(Long localeId){
        if (values != null){
            return values.stream().filter(sc -> localeId.equals(sc.getLocaleId()))
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    public void initValues(){
        if (values == null){
            values = new ArrayList<>();
        }
    }

    public Value getOrCreateValue(Long localeId){
        initValues();

        Value value = getValue(localeId);

        if (value == null){
            value = new Value(localeId);
            values.add(value);
        }

        return value;
    }


    public Value getValue(Locale locale){
        Long localeId = Locales.getLocaleId(locale);

        Value value = localeId != null ? getValue(localeId) : null;

        return value != null || Locales.getSystemLocaleId().equals(localeId) ? value : getValue(Locales.getSystemLocaleId());
    }

    public void setTextValue(String text, Long localeId){
        getOrCreateValue(localeId).setText(text);
    }

    public void addTextValue(String text){
        initValues();

        Value value = new Value();
        value.setText(text);

        values.add(value);
    }

    public void addNumberValue(Long number){
        initValues();

        Value value = new Value();
        value.setNumber(number);

        values.add(value);
    }

    public List<Long> getNumberValues(){
        initValues();

        return values.stream().map(Value::getNumber).collect(Collectors.toList());
    }

    public List<String> getTextValues(){
        initValues();

        return values.stream().map(Value::getText).collect(Collectors.toList());
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public EntityAttribute getEntityAttribute() {
        return entityAttribute;
    }

    public void setEntityAttribute(EntityAttribute entityAttribute) {
        this.entityAttribute = entityAttribute;
    }
}
