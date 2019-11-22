package ru.complitex.domain.util;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import ru.complitex.domain.entity.EntityAttribute;

import java.util.Set;

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

    private static final Set<String> NAMES = Sets.newHashSet("Майкук", "БА", "ПК", "САП", "ММБ", "МК");

    public static String capitalize(String text){
        if (text == null){
            return "";
        }

        String[] words = text.split(" ");

        words[0] = words[0].matches("\\w.*")
                ? words[0].toUpperCase()
                : WordUtils.capitalizeFully(words[0], '-');

        for (int i = 1; i < words.length; i++){
            String world = words[i];

            String name = NAMES.stream().filter(n -> n.equalsIgnoreCase(world)).findFirst().orElse(null);

            if (name != null){
                words[i] = name;
            }else if (world.matches("\\w.*")){
                words[i] = world.toUpperCase();
            }else{
                words[i] = world.toLowerCase();
            }
        }

        return StringUtils.join(words, " ");
    }

    public static String capitalizeWords(String text){
        return WordUtils.capitalizeFully(text, ' ', '-');
    }
}
