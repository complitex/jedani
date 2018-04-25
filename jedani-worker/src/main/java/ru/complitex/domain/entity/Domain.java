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
    private String externalId;

    private String entityName;

    private List<Attribute> attributes = new ArrayList<>();

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
        this.externalId = domain.externalId;
        this.entityName = domain.entityName;
        this.attributes = domain.attributes;
    }

    public Domain(Domain domain, String entityName){
        this(domain);

        this.entityName = entityName;
    }

    public void setText(Long entityAttributeId, String text){
        getOrCreateAttribute(entityAttributeId).setText(text);
    }

    public void setNumber(Long entityAttributeId, String number){
        setNumber(entityAttributeId, Optional.ofNullable(number)
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .orElse(null));
    }

    public void setNumber(Long entityAttributeId, Long number){
        getOrCreateAttribute(entityAttributeId).setNumber(number);
    }

    public void setDate(Long entityAttributeId, Date date){
        getOrCreateAttribute(entityAttributeId).setDate(date);
    }

    public void setJson(Long entityAttributeId, String json){
        getOrCreateAttribute(entityAttributeId).setJson(json);
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

    public String getJson(Long entityAttributeId){
        return Optional.ofNullable(getAttribute(entityAttributeId))
                .map(Attribute::getJson)
                .orElse(null);
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

    public void setValue(Long entityAttributeId, String value, Locale locale){
        getOrCreateAttribute(entityAttributeId).setValue(value, Locales.getLocaleId(locale));
    }

    public void setValue(Long entityAttributeId, String value){
        setValue(entityAttributeId, value, Locales.getSystemLocale());
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

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
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
}
