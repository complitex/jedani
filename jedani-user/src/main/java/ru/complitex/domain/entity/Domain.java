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

    public void setText(Long entityAttributeId, String text){
        getOrCreateAttribute(entityAttributeId).setText(text);
    }

    public void setNumber(Long entityAttributeId, String number){
        setNumber(entityAttributeId, Optional.ofNullable(number).
                filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .orElse(null));
    }

    public void setNumber(Long entityAttributeId, Long number){
        getOrCreateAttribute(entityAttributeId).setNumber(number);
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

        return attribute != null? attribute.getText() : null;
    }

    public Value getValue(Long entityAttributeId, Locale locale){
        Attribute attribute = getAttribute(entityAttributeId);

        return attribute != null ? attribute.getValue(locale) : null;
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

    public void setStatus(Status status) {
        this.status = status;
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