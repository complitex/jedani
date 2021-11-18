package ru.complitex.jedani.worker.service.cache;

import ru.complitex.jedani.worker.service.SaleService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Ivanov Anatoliy
 */
@ApplicationScoped
public class SaleCacheService implements Serializable {
    @Inject
    private SaleService saleService;

    private transient Set<Long> saleWorkerIds;

    public Set<Long> getSaleWorkerIds() {
        if (saleWorkerIds == null) {
            saleWorkerIds = saleService.getSaleWorkerIds();
        }

        return saleWorkerIds;
    }

    public boolean hasSale(Long workerId) {
        return getSaleWorkerIds().contains(workerId);
    }

    public void clear() {
        saleWorkerIds = null;
    }
}
