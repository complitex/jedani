package ru.complitex.name.service;

import com.google.common.base.Strings;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.util.Attributes;
import ru.complitex.name.entity.FirstName;
import ru.complitex.name.entity.LastName;
import ru.complitex.name.entity.MiddleName;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 01.05.2018 3:40
 */
public class NameService implements Serializable {
    @Inject
    private DomainMapper domainMapper;

    private Long getOrCreateName(String entityName, Long entityAttributeId, String name, Long objectId){
        if (Strings.isNullOrEmpty(name)){
            return null;
        }

        name = name.toUpperCase();

        if (objectId != null) {
            Domain<?> domain = domainMapper.getDomain(entityName, objectId);

            if (domain.hasValueText(entityAttributeId, name)){
                return objectId;
            }
        }

        List<Domain<?>> domains = domainMapper.getDomains(FilterWrapper.of(new Domain<>(entityName)
                .setText(entityAttributeId, name)).setFilter("equal"));

        if (!domains.isEmpty()){
            return domains.get(0).getObjectId();
        }

        Domain<?> domain = new Domain<>(entityName);
        domain.setTextValue(entityAttributeId, name);

        domainMapper.insertDomain(domain);

        return domain.getObjectId();
    }

    public Long getOrCreateLastName(String lastName, Long objectId){
        return getOrCreateName(LastName.ENTITY_NAME, LastName.NAME, lastName, objectId);
    }

    public Long getOrCreateFirstName(String firstName, Long objectId){
        return getOrCreateName(FirstName.ENTITY_NAME, FirstName.NAME, firstName, objectId);
    }

    public Long getOrCreateMiddleName(String middleName, Long objectId){
        return getOrCreateName(MiddleName.ENTITY_NAME, MiddleName.NAME, middleName, objectId);
    }

    public String getName(Long objectId, String entityName, Long nameId){
        if (objectId != null){
            Domain<?> domain = domainMapper.getDomain(entityName, objectId);

            if (domain != null){
                return Attributes.capitalize(domain.getTextValue(nameId));
            }

            return "[" + objectId + "]";
        }

        return "";
    }

    public String getLastName(Long objectId){
        return getName(objectId, LastName.ENTITY_NAME, LastName.NAME);
    }

    public String getFirstName(Long objectId){
        return getName(objectId, FirstName.ENTITY_NAME, FirstName.NAME);
    }

    public String getMiddleName(Long objectId){
        return getName(objectId, MiddleName.ENTITY_NAME, MiddleName.NAME);
    }

    public String getFio(Long lastNameId, Long firstNameId, Long middleNameId){
        return getLastName(lastNameId) + " " + getFirstName(firstNameId) + " " + getMiddleName(middleNameId);
    }
}
