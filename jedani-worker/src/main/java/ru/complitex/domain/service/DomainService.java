package ru.complitex.domain.service;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.mapper.DomainMapper;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 29.11.2017 17:54
 */
public class DomainService {
    @Inject
    private DomainMapper domainMapper;

    public <T extends Domain> T newObject(Class<T> domainClass){
        try {
            return domainClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("error create new object " + e);
        }
    }

    public <T extends Domain> T newObject(Class<T> domainClass, Domain domain){
        try {
            T domainInstance = domainClass.newInstance();

            domainInstance.wrap(domain);

            return domainInstance;
        } catch (Exception e) {
            throw new RuntimeException("error create new object " + e);
        }
    }

    public <T extends Domain> List<T> getDomains(Class<T> domainClass, FilterWrapper<T> filterWrapper){
        return domainMapper.getDomains(filterWrapper).stream()
                .map(d -> newObject(domainClass, d))
                .collect(Collectors.toList());
    }

    public <T extends Domain> Long getDomainsCount(FilterWrapper<T> filterWrapper){
        return domainMapper.getDomainsCount(filterWrapper);
    }

    public Domain getDomain(String entityName, Long objectId){
        return domainMapper.getDomain(entityName, objectId);
    }

    public Domain getDomainWithNumberValues(String entityName, Long objectId){
        return domainMapper.getDomain(entityName, objectId, false, true);
    }
}
