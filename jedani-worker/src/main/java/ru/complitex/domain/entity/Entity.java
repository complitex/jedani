package ru.complitex.domain.entity;

import ru.complitex.domain.util.Locales;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 14:27
 */
public class Entity implements Serializable {
    private Long id;
    private String name;
    private List<EntityValue> values;

    private List<EntityAttribute> attributes;

    public EntityAttribute getEntityAttribute(Long entityAttributesId){
        return attributes.stream().filter(a -> a.getEntityAttributeId().equals(entityAttributesId)).findAny()
                .orElseThrow(() -> new RuntimeException(String.format("EntityAttribute not found by id '%s' for '%s'",
                        entityAttributesId, name)));
    }

    public EntityValue getValue(){
        return values.stream().filter(v -> v.getLocaleId().equals(Locales.getSystemLocaleId())).findAny().orElse(null);
    }

    public List<EntityAttribute> getEntityAttributes(Long... entityAttributesId){
        return Stream.of(entityAttributesId).map(this::getEntityAttribute).collect(Collectors.toList());
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
