package ru.complitex.domain.entity;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import ru.complitex.domain.util.Locales;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 15:29
 */
public class Domain implements Serializable{
    public final static String FILTER_ATTRIBUTES = "attributes";
    public final static String FILTER_EXCLUDE_OBJECT_IDS = "excludeObjectIds";

    private Long id;
    private Long objectId;
    private Long parentId;
    private Long parentEntityId;
    private Date startDate;
    private Date endDate;
    private Status status;
    private Long permissionId;
    private Long userId;

    private boolean useDateAttribute;
    private boolean useNumberValue;

    private String entityName;

    private List<Attribute> attributes = new ArrayList<>();

    private Map<String, Object> map = new HashMap<>();

    private EntityAttribute parentEntityAttribute;

    public Domain() {
    }

    public Domain(String entityName) {
        this.entityName = entityName;
    }

    public Domain(Domain domain){
        copy(domain, false);
    }

    public void copy(Domain domain, boolean initAttributes){
       id = domain.id;
       objectId = domain.objectId;
       parentId = domain.parentId;
       parentEntityId = domain.parentEntityId;
       startDate = domain.startDate;
       endDate = domain.endDate;
       status = domain.status;
       permissionId = domain.permissionId;
       entityName = domain.entityName;
       userId = domain.userId;

        if (initAttributes) {
            domain.attributes.forEach(a -> attributes.add(new Attribute(a)));
        }else{
            attributes = domain.attributes;
        }

        map.putAll(domain.map);
    }

    public Domain(Domain domain, String entityName){
        this(domain);

        this.entityName = entityName;
    }

    public Domain(String entityName, Long objectId) {
        this.entityName = entityName;
        this.objectId = objectId;
    }

    public void setUpperText(Long entityAttributeId, String text){
        if (text != null){
            setText(entityAttributeId, text.toUpperCase());
        }
    }

    public void setDate(Long entityAttributeId, Date date){
        getOrCreateAttribute(entityAttributeId).setDate(date);
    }

    public Attribute getOrCreateAttribute(Long entityAttributeId) {
        Attribute attribute = getAttribute(entityAttributeId);

        if (attribute != null) {
            return attribute;
        }

        attribute = new Attribute(entityAttributeId);

        attributes.add(attribute);

        return attribute;
    }

    public Attribute getAttribute(Long entityAttributeId) {
        for (Attribute attribute : attributes) {
            if (attribute.getEntityAttributeId().equals(entityAttributeId) &&  attribute.getEndDate() == null) {
                return attribute;
            }
        }

        return null;
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

    public Domain setText(Long entityAttributeId, String text){
        getOrCreateAttribute(entityAttributeId).setText(text);

        return this;
    }

    public void copyText(Long entityAttributeId, Domain domain){
        setText(entityAttributeId, domain.getText(entityAttributeId));
    }

    public Long getNumber(Long entityAttributeId) {
        Attribute attribute = getAttribute(entityAttributeId);

        return attribute != null ? attribute.getNumber() : null;
    }

    public Long getNumber(Long entityAttributeId, Long defaultNumber){
        Attribute attribute = getAttribute(entityAttributeId);

        return attribute != null ? attribute.getNumber() != null ? attribute.getNumber() : defaultNumber : defaultNumber;
    }

    public Domain setNumber(Long entityAttributeId, Long number){
        getOrCreateAttribute(entityAttributeId).setNumber(number);

        return this;
    }

    public void copyNumber(Long entityAttributeId, Domain domain){
        setNumber(entityAttributeId, domain.getNumber(entityAttributeId));
    }

    public void setBoolean(Long entityAttributeId, Boolean _boolean){
        setNumber(entityAttributeId, _boolean != null ? _boolean ? 1L : 0 : null);
    }

    public Boolean getBoolean(Long entityAttributeId){
        Long number = getNumber(entityAttributeId);

        return number != null ? number.equals(1L) : null;
    }

    public boolean isBoolean(Long entityAttributeId){
        Long number = getNumber(entityAttributeId);

        return number != null && number.equals(1L);
    }

    public BigDecimal getDecimal(Long entityAttributeId) {
        String text = getOrCreateAttribute(entityAttributeId).getText();

        return text != null && !text.isEmpty()  ? new BigDecimal(text) : null;
    }

    public BigDecimal getDecimal(Long entityAttributeId, BigDecimal defaultNumber){
        Attribute attribute = getAttribute(entityAttributeId);

        return attribute != null ? attribute.getDecimal() != null ? attribute.getDecimal() : defaultNumber : defaultNumber;
    }

    public Domain setDecimal(Long entityAttributeId, BigDecimal decimal){
        setText(entityAttributeId, decimal != null ? decimal.stripTrailingZeros().toPlainString() : null);

        return this;
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

    public String getTextValue(Long entityAttributeId, Locale locale){
        return Optional.ofNullable(getAttribute(entityAttributeId))
                .map(a -> a.getValue(locale))
                .map(Value::getText)
                .orElse(null);
    }

    public String getTextValue(Long entityAttributeId){
        return getTextValue(entityAttributeId, Locales.getSystemLocale());
    }

    public void setTextValue(Long entityAttributeId, String value, Locale locale){
        getOrCreateAttribute(entityAttributeId).setTextValue(value, Locales.getLocaleId(locale));
    }

    public void setTextValue(Long entityAttributeId, String value){
        setTextValue(entityAttributeId, value, Locales.getSystemLocale());
    }

    public void setUpperTextValue(Long entityAttributeId, String value){
        if (value != null){
            setTextValue(entityAttributeId, value.toUpperCase());
        }
    }

    public void addTextValue(Long entityAttributeId, String text){
        getOrCreateAttribute(entityAttributeId).addTextValue(text);
    }

    public void addUpperTextValue(Long entityAttributeId, String text){
        if (text != null) {
            addTextValue(entityAttributeId, text.toUpperCase());
        }
    }

    public void addNumberValue(Long entityAttributeId, Long number){
        getOrCreateAttribute(entityAttributeId).addNumberValue(number);
    }

    public List<Long> getNumberValues(Long entityAttributeId){
        return getOrCreateAttribute(entityAttributeId).getNumberValues();
    }

    public String getNumberValuesString(Long entityAttributeId){
        return Strings.emptyToNull(getNumberValues(entityAttributeId).stream().map(Object::toString)
                .collect(Collectors.joining(",")));
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

    public void clearValues(Long entityAttributeId){
        getOrCreateAttribute(entityAttributeId).clearValues();
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

    public Domain setObjectId(Long objectId) {
        this.objectId = objectId;

        return this;
    }

    public Long getParentId() {
        return parentId;
    }

    public Domain setParentId(Long parentId) {
        this.parentId = parentId;

        return this;
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

    public Domain put(String key, Object object){
        map.put(key, object);

        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public EntityAttribute getParentEntityAttribute() {
        return parentEntityAttribute;
    }

    public void setParentEntityAttribute(EntityAttribute parentEntityAttribute) {
        this.parentEntityAttribute = parentEntityAttribute;
    }

    public boolean isUseDateAttribute() {
        return useDateAttribute;
    }

    public void setUseDateAttribute(boolean useDateAttribute) {
        this.useDateAttribute = useDateAttribute;
    }

    public boolean isUseNumberValue() {
        return useNumberValue;
    }

    public void setUseNumberValue(boolean useNumberValue) {
        this.useNumberValue = useNumberValue;
    }

    public Domain setFilter(Long entityAttributeId, String filter){
        getOrCreateAttribute(entityAttributeId).setFilter(filter);

        return this;
    }

    public void zeroToNull() {
        for (Attribute attribute : getAttributes()) {
            if (Objects.equals(attribute.getNumber(), 0L)) {
                attribute.setNumber(null);
            }

            if (Objects.equals(attribute.getDecimal(), ZERO)) {
                attribute.setDecimal(null);
            }
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("id", id)
                .add("objectId", objectId)
                .add("parentId", parentId)
                .add("parentEntityId", parentEntityId)
                .add("startDate", startDate)
                .add("endDate", endDate)
                .add("status", status)
                .add("permissionId", permissionId)
                .add("userId", userId)
                .add("useDateAttribute", useDateAttribute)
                .add("useNumberValue", useNumberValue)
                .add("entityName", entityName)
                .add("attributes", attributes)
                .add("map", map)
                .add("parentEntityAttribute", parentEntityAttribute)
                .toString();
    }
}
