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
    private Long pkId;
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

    public void addAttribute(Attribute attribute) {
        if (attribute != null) {
            attributes.add(attribute);
        }
    }

    public void addAttributePair(Long entityAttributeId1, Long entityAttributeId2){
        Long attributeId = attributes.stream()
                .filter(a -> a.getEntityAttributeId().equals(entityAttributeId1))
                .mapToLong(Attribute::getAttributeId)
                .max()
                .orElse(0) + 1;

        attributes.add(new Attribute(entityAttributeId1, attributeId));
        attributes.add(new Attribute(entityAttributeId2, attributeId));
    }

    public void addAttribute(Long entityAttributeId, Long valueId){
        Long attributeId = attributes.stream()
                .filter(a -> a.getEntityAttributeId().equals(entityAttributeId))
                .mapToLong(Attribute::getAttributeId)
                .max()
                .orElse(0) + 1;

        Attribute attribute = new Attribute();
        attribute.setAttributeId(attributeId);
        attribute.setEntityAttributeId(entityAttributeId);
        attribute.setValueId(valueId);
    }

    public Attribute getAttribute(Long entityAttributeId) {
        return attributes.stream()
                .filter(a -> a.getEntityAttributeId().equals(entityAttributeId))
                .filter(a -> a.getEndDate() == null)
                .findAny()
                .orElse(null);
    }

    public Attribute getAttribute(Long entityAttributeId, Long attributeId){
        return attributes.stream()
                .filter(a -> a.getEndDate() == null)
                .filter(a -> a.getEntityAttributeId().equals(entityAttributeId))
                .filter(a -> a.getAttributeId().equals(attributeId))
                .findAny()
                .orElse(null);
    }

    public List<Attribute> getAttributes(Long entityAttributeId) {
        return attributes.stream()
                .filter(a -> a.getEntityAttributeId().equals(entityAttributeId))
                .filter(a -> a.getEndDate() == null)
                .collect(Collectors.toList());
    }

    public List<Long> getValueIds(Long entityAttributeId){
        return attributes.stream()
                .filter(a -> a.getEntityAttributeId().equals(entityAttributeId))
                .filter(a -> a.getEndDate() == null)
                .map(Attribute::getValueId)
                .collect(Collectors.toList());
    }

    public void removeAttribute(Long entityAttributeId) {
        attributes.removeIf(attribute -> attribute.getEntityAttributeId().equals(entityAttributeId));
    }

    public void removeAttribute(Long entityAttributeId, Long attributeId){
        attributes.removeIf(a -> a.getEntityAttributeId().equals(entityAttributeId) && a.getAttributeId().equals(attributeId));
    }

    public String getStringValue(Long entityAttributeId){
        Attribute attribute = getAttribute(entityAttributeId);

        return attribute != null? attribute.getStringValue() : null;
    }

    public String getStringValue(Long entityAttributeId, Locale locale){
        Attribute attribute = getAttribute(entityAttributeId);

        return attribute != null ? attribute.getStringValue(locale) : null;
    }

    public void setStringValue(Long entityAttributeId, String value, Locale locale){
        getAttribute(entityAttributeId).setStringValue(value, Locales.getLocaleId(locale));
    }

    public void setStringValue(Long entityAttributeId, String value){
        setStringValue(entityAttributeId, value, Locales.getSystemLocale());
    }

    public Map<String, String> getStringMap(Long entityAttributeId){
        if (getAttribute(entityAttributeId) == null || getAttribute(entityAttributeId).getStringValues().isEmpty()){
            return null;
        }

        return getAttribute(entityAttributeId).getStringValues().stream()
                .filter(s -> s.getValue() != null)
                .collect(Collectors.toMap(s -> Locales.getLanguage(s.getLocaleId()), StringValue::getValue));
    }

    public Long getValueId(Long entityAttributeId){
        Attribute attribute =  getAttribute(entityAttributeId);

        return attribute != null ? attribute.getValueId() : null;
    }

    public Long getPkId() {
        return pkId;
    }

    public void setPkId(Long pkId) {
        this.pkId = pkId;
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
