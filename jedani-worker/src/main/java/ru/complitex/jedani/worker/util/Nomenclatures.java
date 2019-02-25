package ru.complitex.jedani.worker.util;

import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.jedani.worker.entity.Nomenclature;

/**
 * @author Anatoly A. Ivanov
 * 24.02.2019 7:01
 */
public class Nomenclatures {
    public static String getNomenclatureLabel(Domain nomenclature){
        if (nomenclature == null){
            return "";
        }

        String code = nomenclature.getText(Nomenclature.CODE);

        return (code != null ? code + ", " : "") + Attributes.capitalize(nomenclature.getTextValue(Nomenclature.NAME));
    }

    public static String getNomenclatureLabel(Long nomenclatureId, DomainService domainService){
        if (nomenclatureId == null){
            return "";
        }

        return getNomenclatureLabel(domainService.getDomain(Nomenclature.class, nomenclatureId));
    }
}
