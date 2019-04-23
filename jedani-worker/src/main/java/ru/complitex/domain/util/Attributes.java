package ru.complitex.domain.util;

import ru.complitex.domain.entity.EntityAttribute;

/**
 * @author Anatoly A. Ivanov
 * 18.09.2018 18:16
 */
public class Attributes {
    public static String displayText(EntityAttribute entityAttribute, String text){
        if (text == null){
            return null;
        }

        if (entityAttribute != null) {
            switch (entityAttribute.getStringType()){
                case LOWER_CASE:
                    return text.toLowerCase();
                case UPPER_CASE:
                    return text.toUpperCase();
                case CAPITALIZE:
                    return capitalize(text);
            }
        }

        return text;
    }

    public static String capitalize(String text){
        if (text == null || text.isEmpty()){
            return text;
        }

        String[] arr = text.toLowerCase().split(" ");
        StringBuilder sb = new StringBuilder();

        for (String s : arr) {
            sb.append(Character.toUpperCase(s.charAt(0)))
                    .append(s.substring(1)).append(" ");
        }

        return sb.toString().trim();
    }
}
