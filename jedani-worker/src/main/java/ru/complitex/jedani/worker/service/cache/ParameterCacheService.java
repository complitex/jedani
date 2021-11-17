package ru.complitex.jedani.worker.service.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.RewardParameter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Ivanov Anatoliy
 */
@ApplicationScoped
public class ParameterCacheService implements Serializable {
    @Inject
    private DomainService domainService;

    private transient LoadingCache<Long, BigDecimal> parameterCache;

    private LoadingCache<Long, BigDecimal> getParameterCache() {
        if (parameterCache == null) {
            parameterCache = CacheBuilder.newBuilder()
                    .build(CacheLoader.from(rewardParameterId ->
                            domainService.getDomain(RewardParameter.class, rewardParameterId)
                                    .getDecimal(RewardParameter.VALUE)));

        }

        return parameterCache;
    }

    public BigDecimal getParameter(Long rewardParameterId) {
        try {
            return getParameterCache().get(rewardParameterId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        getParameterCache().invalidateAll();
    }
}
