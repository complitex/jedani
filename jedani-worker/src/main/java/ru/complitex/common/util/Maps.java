package ru.complitex.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov
 * 22.01.2018 21:34
 */
public class Maps {
    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8){
        Map<K,V> map = new HashMap<>(8);

        if (v1 != null) map.put(k1, v1);
        if (v2 != null) map.put(k2, v2);
        if (v3 != null) map.put(k3, v3);
        if (v4 != null) map.put(k4, v4);
        if (v5 != null) map.put(k5, v5);
        if (v6 != null) map.put(k6, v6);
        if (v7 != null) map.put(k7, v7);

        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        return of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, null, null);
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        return of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, null, null);
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, null, null);
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return of(k1, v1, k2, v2, k3, v3, k4, v4, null, null);
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        return of(k1, v1, k2, v2, k3, v3, null, null);
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
        return of(k1, v1, k2, v2, null, null);
    }

    public static <K, V> Map<K, V> of(K k1, V v1) {
        return of(k1, v1, null, null);
    }

}
