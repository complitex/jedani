package ru.complitex.domain.util;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 15:48
 */
public class Locales {
    private Long systemLocaleId;

    private Locale systemLocale;

    private Map<Locale, Long> map = new ConcurrentHashMap<>();
    private Map<Long, Locale> mapId = new ConcurrentHashMap<>();

    public static final Locale RU = new Locale("ru");
    public static final Locale UA = new Locale("uk");

    public static final Long RU_ID = 1L;
    public static final Long UA_ID = 2L;

    private static Locales instance = new Locales();

    private Map<Class, Properties> propertiesMap = new ConcurrentHashMap<>();

    public Locales() {
        map.put(RU, RU_ID);
        map.put(UA, UA_ID);

        mapId.put(RU_ID, RU);
        mapId.put(UA_ID, UA);

        systemLocale = RU;
        systemLocaleId = RU_ID;
    }

    public static Locale getSystemLocale() {
        return instance.systemLocale;
    }

    public static Long getSystemLocaleId() {
        return instance.systemLocaleId;
    }

    public static Long getLocaleId(Locale locale){
        return instance.map.get(locale);
    }

    public static Collection<Long> getLocaleIds(){
        return instance.map.values();
    }

    public static Locale getLocale(Long localeId){
        return instance.mapId.get(localeId);
    }

    public static String getLanguage(Long localeId){
        return instance.mapId.get(localeId).getLanguage();
    }

    private static Properties getProperties(Class _class){
        Properties properties = instance.propertiesMap.get(_class);

        if (properties == null){
            properties = new Properties();

            try {
                properties.load(_class.getResourceAsStream(_class.getSimpleName() + ".properties"));

                instance.propertiesMap.put(_class, properties);
            } catch (IOException e) {
                return null;
            }
        }

        return properties;
    }

    public static String getString(Class _class, String key){
        return Objects.requireNonNull(getProperties(_class)).getProperty(key);
    }
}
