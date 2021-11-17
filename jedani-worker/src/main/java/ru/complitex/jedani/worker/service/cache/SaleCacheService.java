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

    private transient Set<Long> activeSaleWorkerIds;

    public Set<Long> getActiveSaleWorkerIds() {
        if (activeSaleWorkerIds == null) {
            activeSaleWorkerIds = saleService.getActiveSaleWorkerIds();
        }

        return activeSaleWorkerIds;
    }

    public boolean hasActiveSale(Long workerId) {
        return getActiveSaleWorkerIds().contains(workerId);
    }

    public void clear() {
        activeSaleWorkerIds = null;
    }
}
