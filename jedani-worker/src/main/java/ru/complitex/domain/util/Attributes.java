package ru.complitex.domain.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import ru.complitex.domain.entity.EntityAttribute;

import java.util.Arrays;
import java.util.stream.Collectors;

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
                case CAPITALIZE_WORDS:
                    return capitalizeWords(text);
            }
        }

        return text;
    }

    public static String capitalize(String text){
        if (text == null){
            return "";
        }

        text = StringUtils.capitalize(text.toLowerCase());

        return Arrays.stream(text.split(" ")).map(s ->{
            if (s.matches("\\w.*")){
                return s.toUpperCase();
            }

            return s;
        }).collect(Collectors.joining(" "));
    }

    public static String capitalizeWords(String text){
        return WordUtils.capitalizeFully(text, '-');
    }
}
