package ru.complitex.jedani.worker.service.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.tuple.Pair;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.mapper.RewardParameterMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Ivanov Anatoliy
 */
@ApplicationScoped
public class RewardParameterCacheService implements Serializable {
     @Inject
    private PeriodMapper periodMapper;

     @Inject
     private RewardParameterMapper rewardParameterMapper;

    private transient LoadingCache<Pair<Long, Long>, BigDecimal> rewardParameterCache;

    private LoadingCache<Pair<Long, Long>, BigDecimal> getRewardParameterCache() {
        if (rewardParameterCache == null) {
            rewardParameterCache = CacheBuilder.newBuilder()
                    .build(CacheLoader.from(pair -> {
                        Period period = periodMapper.getPeriod(pair.getRight());

                        return rewardParameterMapper.getRewardParameterValue(pair.getLeft(), period.getOperatingMonth());
                    }));

        }

        return rewardParameterCache;
    }

    public BigDecimal getParameter(Long rewardParameterId, Long periodId) {
        try {
            return getRewardParameterCache().get(Pair.of(rewardParameterId, periodId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        getRewardParameterCache().invalidateAll();
    }
}
