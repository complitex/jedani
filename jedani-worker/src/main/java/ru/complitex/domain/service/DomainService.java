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

    @Inject
    private EntityService entityService;

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

    public String getEntityName(Long entityId){
        return entityService.getEntity(entityId).getName();
    }

    public Domain getDomainRef(Long referenceId, Long objectId){
        return domainMapper.getDomain(getEntityName(referenceId), objectId);
    }

    public <T extends Domain> List<T> getDomainsByParentId(Class<T> domainClass, Long parentId){
        T domain = Domains.newObject(domainClass);
        domain.setParentId(parentId);

        return getDomains(domainClass, FilterWrapper.of(domain));
    }

    @Transactional
    public void save(Domain domain){
        if (domain.getObjectId() != null){
            domainMapper.updateDomain(domain);
        }else{
            domainMapper.insertDomain(domain);
        }
    }

    @Transactional
    public void insertDomain(Domain domain){
        domainMapper.insertDomain(domain);
    }

    @Transactional
    public void updateDomain(Domain domain){
        domainMapper.updateDomain(domain);
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

    public String getTextOrEmpty(String entityName, Long objectId, Long entityAttributeId){
        String s =  attributeMapper.getText(entityName, objectId, entityAttributeId);

        return s != null ? s : "";
    }

    public String getTextValue(String entityName, Long objectId, Long entityAttributeId){
        return attributeMapper.getTextValue(entityName, objectId, entityAttributeId);
    }

    public void delete(Domain domain){
        domainMapper.delete(domain);
    }
}
