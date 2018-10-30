package ru.complitex.domain.util;

import ru.complitex.domain.entity.Domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov
 * 30.10.2018 15:03
 */
public class Domains {
    private static Map<Class, Domain> domainMap = new HashMap<>();

    public static <T extends Domain> T newObject(Class<T> domainClass){
        try {
            return domainClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("error create new object " + e);
        }
    }

    public static <T extends Domain> T newObject(Class<T> domainClass, Domain domain){
        if (domain == null){
            return null;
        }

        try {
            T domainInstance = domainClass.newInstance();

            domainInstance.wrap(domain);

            return domainInstance;
        } catch (Exception e) {
            throw new RuntimeException("error create new object " + e);
        }
    }

    private static <T extends Domain> Domain getDomain(Class<T> domainClass){
        Domain domain = domainMap.get(domainClass);

        if (domain == null){
            domain = newObject(domainClass);

            domainMap.put(domainClass, domain);
        }

        return domain;
    }

    public static <T extends Domain> String getEntityName(Class<T> domainClass){
        return getDomain(domainClass).getEntityName();
    }

    public static <T extends Domain> boolean isUseNumberValue(Class<T> domainClass){
        return getDomain(domainClass).isUseNumberValue();
    }

    public static <T extends Domain> boolean isUseDateAttribute(Class<T> domainClass){
        return getDomain(domainClass).isUseDateAttribute();
    }
}
