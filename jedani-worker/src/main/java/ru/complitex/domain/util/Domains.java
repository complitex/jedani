package ru.complitex.domain.util;

import ru.complitex.domain.entity.Domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov
 * 30.10.2018 15:03
 */
public class Domains {
    private static final Map<Class<?>, Domain<?>> domainMap = new HashMap<>();

    public static <T extends Domain<T>> T newObject(Class<T> domainClass){
        try {
            return domainClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("error create new object " + e);
        }
    }

    public static <T extends Domain<T>> T newObject(Class<T> domainClass, Domain<?> domain, boolean initAttributes){
        if (domain == null){
            return null;
        }

        try {
            T domainInstance = domainClass.getConstructor().newInstance();

            domainInstance.copy(domain, initAttributes);

            return domainInstance;
        } catch (Exception e) {
            throw new RuntimeException("error create new object " + e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Domain<T>> T copy(T domain){
        if (domain == null){
            return null;
        }

        try {
            T domainInstance = (T) domain.getClass().newInstance();

            domainInstance.copy(domain, true);

            return domainInstance;
        } catch (Exception e) {
            throw new RuntimeException("error create new object " + e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Domain<T>> T getDomain(Class<T> domainClass){
        T domain = (T) domainMap.get(domainClass);

        if (domain == null){
            domain = newObject(domainClass);

            domainMap.put(domainClass, domain);
        }

        return domain;
    }

    public static <T extends Domain<T>> String getEntityName(Class<T> domainClass){
        return getDomain(domainClass).getEntityName();
    }

    public static <T extends Domain<T>> boolean isUseNumberValue(Class<T> domainClass){
        return getDomain(domainClass).isUseNumberValue();
    }

    public static <T extends Domain<T>> boolean isUseDateAttribute(Class<T> domainClass){
        return getDomain(domainClass).isUseDateAttribute();
    }
}
