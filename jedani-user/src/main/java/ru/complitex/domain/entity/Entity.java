package ru.complitex.domain.entity;

import ru.complitex.domain.util.Locales;

import java.io.Serializable;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 14:27
 */
public class Entity implements Serializable {
    private Long id;
    private String name;
    private List<EntityValue> values;

    private List<EntityAttribute> attributes;

    public EntityValue getValue(){
        return values.stream().filter(v -> v.getLocaleId().equals(Locales.getSystemLocaleId())).findAny().orElse(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<EntityValue> getValues() {
        return values;
    }

    public void setValues(List<EntityValue> values) {
        this.values = values;
    }

    public List<EntityAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<EntityAttribute> attributes) {
        this.attributes = attributes;
    }
}
