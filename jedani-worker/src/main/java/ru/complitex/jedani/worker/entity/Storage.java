package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 17.10.2018 16:01
 */
public class Storage extends Domain {
    public static final String ENTITY_NAME = "storage";

    public static final long CITY = 1;
    public static final long WORKERS = 2;
    public static final long TYPE = 3;

    private Long nomenclatureCount;
    private Long transactionCount;

    public Storage() {
        super(ENTITY_NAME);

        setUseNumberValue(true);
    }

    public Long getNomenclatureCount() {
        return nomenclatureCount;
    }

    public void setNomenclatureCount(Long nomenclatureCount) {
        this.nomenclatureCount = nomenclatureCount;
    }

    public Long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Long transactionCount) {
        this.transactionCount = transactionCount;
    }
}
