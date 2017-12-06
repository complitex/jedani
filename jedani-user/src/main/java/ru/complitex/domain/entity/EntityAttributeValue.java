package ru.complitex.domain.entity;

/**
 * @author Anatoly A. Ivanov
 * 06.12.2017 17:37
 */
public class EntityAttributeValue {
    private Long id;
    private Long entityAttributeId;
    private Long localeId;
    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntityAttributeId() {
        return entityAttributeId;
    }

    public void setEntityAttributeId(Long entityAttributeId) {
        this.entityAttributeId = entityAttributeId;
    }

    public Long getLocaleId() {
        return localeId;
    }

    public void setLocaleId(Long localeId) {
        this.localeId = localeId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
