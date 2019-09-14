package ru.complitex.domain.entity;

import com.google.common.base.MoreObjects;
import ru.complitex.domain.util.Locales;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 15:29
 */
public class Attribute implements Serializable{
    public final static String FILTER_DATE = "date";

    private Long id;
    private Long domainId;
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

    private Long objectId;

    public Attribute() {
    }

    public Attribute(Long entityAttributeId) {
        this.entityAttributeId = entityAttributeId;
    }

    public Attribute(String entityName){
        this.entityName = entityName;
    }

    public Attribute(String entityName, Long entityAttributeId){
        this.entityName = entityName;
        this.entityAttributeId = entityAttributeId;
    }

    public Attribute(Attribute attribute){
        copy(attribute);
    }

    public void copy(Attribute attribute){
        id = attribute.id;
        domainId = attribute.domainId;
        entityAttributeId = attribute.entityAttributeId;
        text = attribute.text;
        number = attribute.number;
        date = attribute.date;
        startDate = attribute.startDate;
        endDate = attribute.endDate;
        status = attribute.status;
        userId = attribute.userId;

        entityName = attribute.entityName;

        attribute.values.forEach(v -> values.add(new Value(v)));

        entityAttribute = attribute.entityAttribute;
        objectId = attribute.objectId;
    }

    public Value getValue(Long localeId){
        if (values != null){
            return values.stream().filter(sc -> localeId.equals(sc.getLocaleId()))
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    public Value getOrCreateValue(Long localeId){
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

    public String getTextValue(Long entityAttributeId, Locale locale){
        return Optional.ofNullable(getValue(locale)).map(Value::getText).orElse(null);
    }

    public String getTextValue(Long entityAttributeId){
        return Optional.ofNullable(getValue(Locales.getSystemLocale())).map(Value::getText).orElse(null);
    }

    public void setTextValue(String text, Long localeId){
        getOrCreateValue(localeId).setText(text);
    }

    public void addTextValue(String text){
        Value value = new Value();
        value.setText(text);

        values.add(value);
    }

    public void addNumberValue(Long number){
        Value value = new Value();
        value.setNumber(number);

        values.add(value);
    }

    public List<Long> getNumberValues(){
        return values.stream().map(Value::getNumber).collect(Collectors.toList());
    }

    public List<String> getTextValues(){
        return values.stream().map(Value::getText).collect(Collectors.toList());
    }

    public Attribute setObjectId(Long objectId){
        this.objectId = objectId;

        return this;
    }

    public boolean isEmpty(){
        return text == null && number == null && date == null && values.stream()
                .allMatch(v -> v.getText() == null && v.getNumber() == null);
    }

    public void clearValues(){
        values.clear();
    }

    public Long getObjectId(){
        return objectId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDomainId(){
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
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

    public BigDecimal getDecimal(){
        return text != null && !text.isEmpty()  ? new BigDecimal(text) : null;
    }

    public void setDecimal(BigDecimal decimal){
        text = decimal != null ? decimal.toPlainString() : null;
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("id", id)
                .add("domainId", domainId)
                .add("entityAttributeId", entityAttributeId)
                .add("text", text)
                .add("number", number)
                .add("date", date)
                .add("startDate", startDate)
                .add("endDate", endDate)
                .add("status", status)
                .add("userId", userId)
                .add("entityName", entityName)
                .add("values", values)
                .add("entityAttribute", entityAttribute)
                .add("objectId", objectId)
                .toString();
    }
}
