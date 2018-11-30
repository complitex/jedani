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

    private Long productCount;
    private Long productIntoCount;
    private Long productFromCount;

    public Storage() {
        super(ENTITY_NAME);

        setUseNumberValue(true);
    }

    public Long getProductCount() {
        return productCount;
    }

    public void setProductCount(Long productCount) {
        this.productCount = productCount;
    }

    public Long getProductIntoCount() {
        return productIntoCount;
    }

    public void setProductIntoCount(Long productIntoCount) {
        this.productIntoCount = productIntoCount;
    }

    public Long getProductFromCount() {
        return productFromCount;
    }

    public void setProductFromCount(Long productFromCount) {
        this.productFromCount = productFromCount;
    }
}
