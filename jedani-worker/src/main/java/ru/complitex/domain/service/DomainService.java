package ru.complitex.domain.service;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Domain;
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

    public <T extends Domain> List<T> getDomains(Class<T> domainClass, FilterWrapper<T> filterWrapper){
        return domainMapper.getDomains(filterWrapper).stream()
                .map(d -> Domains.newObject(domainClass, d))
                .collect(Collectors.toList());
    }

    public <T extends Domain> Long getDomainsCount(FilterWrapper<T> filterWrapper){
        return domainMapper.getDomainsCount(filterWrapper);
    }

    public Domain getDomain(String entityName, Long objectId){
        return domainMapper.getDomain(entityName, objectId);
    }

    public <T extends Domain> T getDomain(Class<T> domainClass, Long objectId){
        return Domains.newObject(domainClass, domainMapper.getDomain(Domains.getEntityName(domainClass), objectId,
                Domains.isUseDateAttribute(domainClass), Domains.isUseNumberValue(domainClass)));
    }

    public void save(Domain domain){
        if (domain.getObjectId() != null){
            domainMapper.updateDomain(domain);
        }else{
            domainMapper.insertDomain(domain);
        }
    }
}
