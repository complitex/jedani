package ru.complitex.domain.util;

import ru.complitex.domain.entity.Domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov
 * 30.10.2018 15:03
 */
public class Domains {
    private static Map<Class, String> entityNameMap = new HashMap<>();

    public static <T extends Domain> T newObject(Class<T> domainClass){
        try {
            return domainClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("error create new object " + e);
        }
    }

    public static <T extends Domain> T newObject(Class<T> domainClass, Domain domain){
        try {
            T domainInstance = domainClass.newInstance();

            domainInstance.wrap(domain);

            return domainInstance;
        } catch (Exception e) {
            throw new RuntimeException("error create new object " + e);
        }
    }

    public static <T extends Domain> String getEntityName(Class<T> domainClass){
        String entityName = entityNameMap.get(domainClass);

        if (entityName == null){
            entityName = newObject(domainClass).getEntityName();

            entityNameMap.put(domainClass, entityName);
        }

        return entityName;
    }

}
