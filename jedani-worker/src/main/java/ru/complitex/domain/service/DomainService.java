package ru.complitex.domain.service;

import org.mybatis.cdi.Transactional;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.mapper.AttributeMapper;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.util.Domains;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 29.11.2017 17:54
 */
public class DomainService implements Serializable {
    @Inject
    private DomainMapper domainMapper;

    @Inject
    private AttributeMapper attributeMapper;

    public <T extends Domain> List<T> getDomains(Class<T> domainClass, FilterWrapper<T> filterWrapper, boolean wrapAttributes){
        return domainMapper.getDomains(filterWrapper).stream()
                .map(d -> Domains.newObject(domainClass, d, wrapAttributes))
                .collect(Collectors.toList());
    }

    public <T extends Domain> List<T> getDomains(Class<T> domainClass, FilterWrapper<T> filterWrapper){
        return getDomains(domainClass, filterWrapper, false);
    }

    public <T extends Domain> Long getDomainsCount(FilterWrapper<T> filterWrapper){
        return domainMapper.getDomainsCount(filterWrapper);
    }

    public Domain getDomain(String entityName, Long objectId){
        return domainMapper.getDomain(entityName, objectId);
    }

    public <T extends Domain> T getDomain(Class<T> domainClass, Long objectId, boolean wrapAttributes){
        return Domains.newObject(domainClass, domainMapper.getDomain(Domains.getEntityName(domainClass), objectId,
                Domains.isUseDateAttribute(domainClass), Domains.isUseNumberValue(domainClass)), wrapAttributes);
    }

    public <T extends Domain> T getDomain(Class<T> domainClass, Long objectId){
        if (objectId == null){
            return null;
        }

        return getDomain(domainClass, objectId, false);
    }

    @Transactional
    public void save(Domain domain){
        if (domain.getObjectId() != null){
            domainMapper.updateDomain(domain);
        }else{
            domainMapper.insertDomain(domain);
        }
    }

    public Long getNumber(String entityName, Long objectId, Long entityAttributeId){
        return attributeMapper.getNumber(entityName, objectId, entityAttributeId);
    }

    public List<Long> getNumberValues(String entityName, Long objectId, Long entityAttributeId){
        return attributeMapper.getNumberValues(entityName, objectId, entityAttributeId);
    }

    public String getText(String entityName, Long objectId, Long entityAttributeId){
        return attributeMapper.getText(entityName, objectId, entityAttributeId);
    }

    public String getTextValue(String entityName, Long objectId, Long entityAttributeId){
        return attributeMapper.getTextValue(entityName, objectId, entityAttributeId);
    }
}
