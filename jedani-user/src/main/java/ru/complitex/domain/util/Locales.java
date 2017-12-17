package ru.complitex.domain.util;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
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

    private static Locales instance = new Locales();

    public Locales() {
        map.put(RU, 1L);
        map.put(UA, 2L);

        mapId.put(1L, RU);
        mapId.put(2L, UA);

        systemLocale = RU;
        systemLocaleId = 1L;
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
}