package ru.complitex.domain.entity;

import ru.complitex.domain.util.Locales;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 15:29
 */
public class Domain implements Serializable{
    private Long id;
    private Long objectId;
    private Long parentId;
    private Long parentEntityId;
    private Date startDate;
    private Date endDate;
    private Status status;
    private Long permissionId;

    private String entityName;

    private List<Attribute> attributes = new ArrayList<>();

    private Map<String, Object> map = new HashMap<>();

    public Domain() {
    }

    public Domain(String entityName) {
        this.entityName = entityName;
    }

    public Domain(Domain domain){
        this.id = domain.id;
        this.objectId = domain.objectId;
        this.parentId = domain.parentId;
        this.parentEntityId = domain.parentEntityId;
        this.startDate = domain.startDate;
        this.endDate = domain.endDate;
        this.status = domain.status;
        this.permissionId = domain.permissionId;
        this.entityName = domain.entityName;
        this.attributes = domain.attributes;
        this.map = domain.map;
    }

    public Domain(Domain domain, String entityName){
        this(domain);

        this.entityName = entityName;
    }

    public Domain(String entityName, Long objectId) {
        this.entityName = entityName;
        this.objectId = objectId;
    }

    public Domain setText(Long entityAttributeId, String text){
        getOrCreateAttribute(entityAttributeId).setText(text);

        return this;
    }

    public Domain setNumber(Long entityAttributeId, Long number){
        getOrCreateAttribute(entityAttributeId).setNumber(number);

        return this;
    }

    public void setDate(Long entityAttributeId, Date date){
        getOrCreateAttribute(entityAttributeId).setDate(date);
    }

    public Attribute getOrCreateAttribute(Long entityAttributeId) {
        return attributes.stream()
                .filter(a -> a.getEntityAttributeId().equals(entityAttributeId))
                .filter(a -> a.getEndDate() == null)
                .findAny()
                .orElseGet(() -> {
                    Attribute attribute = new Attribute(entityAttributeId);
                    attributes.add(attribute);

                    return attribute;
                });
    }


    public Attribute getAttribute(Long entityAttributeId) {
        return attributes.stream()
                .filter(a -> a.getEntityAttributeId().equals(entityAttributeId))
                .filter(a -> a.getEndDate() == null)
                .findAny()
                .orElse(null);
    }

    public List<Attribute> getAttributes(Long entityAttributeId) {
        return attributes.stream()
                .filter(a -> a.getEntityAttributeId().equals(entityAttributeId))
                .filter(a -> a.getEndDate() == null)
                .collect(Collectors.toList());
    }

    public void removeAttribute(Long entityAttributeId) {
        attributes.removeIf(attribute -> attribute.getEntityAttributeId().equals(entityAttributeId));
    }

    public String getText(Long entityAttributeId){
        Attribute attribute = getAttribute(entityAttributeId);

        return attribute != null ? attribute.getText() : null;
    }

    public Long getNumber(Long entityAttributeId){
        Attribute attribute = getAttribute(entityAttributeId);

        return attribute != null ? attribute.getNumber() : null;
    }

    public Date getDate(Long entityAttributeId){
        return Optional.ofNullable(getAttribute(entityAttributeId))
                .map(Attribute::getDate)
                .orElse(null);
    }

    public Value getValue(Long entityAttributeId, Locale locale){
        Attribute attribute = getAttribute(entityAttributeId);

        return attribute != null ? attribute.getValue(locale) : null;
    }

    public String getValueText(Long entityAttributeId, Locale locale){
        return Optional.ofNullable(getAttribute(entityAttributeId))
                .map(a -> a.getValue(locale))
                .map(Value::getText)
                .orElse(null);
    }

    public String getValueText(Long entityAttributeId){
        return getValueText(entityAttributeId, Locales.getSystemLocale());
    }

    public void setTextValue(Long entityAttributeId, String value, Locale locale){
        getOrCreateAttribute(entityAttributeId).setTextValue(value, Locales.getLocaleId(locale));
    }

    public void setTextValue(Long entityAttributeId, String value){
        setTextValue(entityAttributeId, value, Locales.getSystemLocale());
    }

    public void addTextValue(Long entityAttributeId, String text){
        getOrCreateAttribute(entityAttributeId).addTextValue(text);
    }

    public void addNumberValue(Long entityAttributeId, Long number){
        getOrCreateAttribute(entityAttributeId).addNumberValue(number);
    }

    public List<Long> getNumberValues(Long entityAttributeId){
        return getOrCreateAttribute(entityAttributeId).getNumberValues();
    }

    public List<String> getTextValues(Long entityAttributeId){
        return getOrCreateAttribute(entityAttributeId).getTextValues();
    }

    public boolean hasValueText(Long entityAttributeId, String value){
        return getAttribute(entityAttributeId).getValues().stream().anyMatch(v -> v.getText().equals(value));
    }

    public Map<String, String> getStringMap(Long entityAttributeId){
        if (getAttribute(entityAttributeId) == null || getAttribute(entityAttributeId).getValues().isEmpty()){
            return null;
        }

        return getAttribute(entityAttributeId).getValues().stream()
                .filter(s -> s.getText() != null)
                .collect(Collectors.toMap(s -> Locales.getLanguage(s.getLocaleId()), Value::getText));
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getParentEntityId() {
        return parentEntityId;
    }

    public void setParentEntityId(Long parentEntityId) {
        this.parentEntityId = parentEntityId;
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

    public Domain setStatus(Status status) {
        this.status = status;

        return this;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
