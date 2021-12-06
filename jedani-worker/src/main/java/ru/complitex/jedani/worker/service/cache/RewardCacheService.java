package ru.complitex.jedani.worker.service.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.tuple.Pair;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Reward;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static java.math.BigDecimal.ZERO;

/**
 * @author Ivanov Anatoliy
 */
@ApplicationScoped
public class RewardCacheService implements Serializable {
    @Inject
    private DomainService domainService;

    @Inject
    private WorkerService workerService;

    private transient LoadingCache<Pair<Long, Long>, List<Reward>> rewardsCache;

    private LoadingCache<Pair<Long, Long>, List<Reward>> getRewardsCache() {
        if (rewardsCache == null) {
            rewardsCache = CacheBuilder.newBuilder()
                    .build(CacheLoader.from(pair ->
                            domainService.getDomains(Reward.class, FilterWrapper.of(new Reward()
                                    .setWorkerId(pair.getLeft())
                                    .setPeriodId(pair.getRight())))));
        }

        return rewardsCache;
    }

    public List<Reward> getRewardsFromCache(Long workerId, Long periodId) {
        try {
            return getRewardsCache().get(Pair.of(workerId, periodId));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public BigDecimal getRewardsPointSumByWorker(Long workerId, Long rewardTypeId, Long rewardStatusId, Long periodId) {
        return getRewardsFromCache(workerId, periodId).stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId))
                .filter(r -> Objects.equals(r.getRewardStatus(), rewardStatusId))
                .reduce(ZERO, ((t, p) -> t.add(p.getPoint())), BigDecimal::add);
    }

    public BigDecimal getRewardsLocal(Long workerId, Long rewardStatusId, Long periodId) {
        return getRewardsFromCache(workerId, periodId).stream()
                .filter(r -> Objects.equals(r.getRewardStatus(), rewardStatusId))
                .map(r -> r.getAmount() != null ? r.getAmount() : ZERO)
                .reduce(ZERO, BigDecimal::add);
    }

    public BigDecimal getRewardsLocalByCurrency(Long rewardStatusId, Long currencyId, Long periodId) {
        return getRewardsFromCache(null, periodId).stream()
                .filter(r -> Objects.equals(r.getRewardStatus(), rewardStatusId))
                .filter(r -> Objects.equals(workerService.getCurrencyId(r.getWorkerId()), currencyId))
                .map(r -> r.getAmount() != null ? r.getAmount() : ZERO)
                .reduce(ZERO, BigDecimal::add);
    }

    public void clear() {
        getRewardsCache().invalidateAll();
    }
}
