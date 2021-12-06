package ru.complitex.jedani.worker.service.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.jedani.worker.entity.RewardNode;
import ru.complitex.jedani.worker.entity.RewardTree;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.service.RewardTreeService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

/**
 * @author Ivanov Anatoliy
 */
@ApplicationScoped
public class RewardTreeCacheService implements Serializable {
    private final Logger log = LoggerFactory.getLogger(RewardTreeCacheService.class);

    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private RewardTreeService rewardTreeService;

    private transient LoadingCache<Long, RewardTree> rewardTreeCache;

    private LoadingCache<Long, RewardTree> getRewardTreeCache() {
        if (rewardTreeCache == null) {
            rewardTreeCache = CacheBuilder.newBuilder()
                    .build(CacheLoader.from(periodId -> {
                        try {
                            return rewardTreeService.getRewardTree(periodId);
                        } catch (Exception e) {
                            log.error("error getRewardTreeCache", e);

                           throw e;
                        }
                    }));
        }

        return rewardTreeCache;
    }

    public RewardTree getRewardTreeCache(Long periodId) {
        try {
            return getRewardTreeCache().get(periodId);
        } catch (ExecutionException e) {
            throw  new RuntimeException(e);
        }
    }

    public RewardNode getRewardNode(Long workerId) {
        return getRewardTreeCache(periodMapper.getActualPeriod().getObjectId()).getRewardNode(workerId);
    }

    public RewardNode getRewardNode(Long periodId, Long workerId) {
        return getRewardTreeCache(periodId).getRewardNode(workerId);
    }

    public void clear() {
        getRewardTreeCache().invalidateAll();
    }
}
