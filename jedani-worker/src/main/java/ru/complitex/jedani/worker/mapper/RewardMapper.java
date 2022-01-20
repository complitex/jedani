package ru.complitex.jedani.worker.mapper;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.common.util.Maps;
import ru.complitex.jedani.worker.entity.Reward;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 20.11.2019 5:25 PM
 */
public class RewardMapper extends BaseMapper {
    public List<Reward> getRewards(FilterWrapper<Reward> filterWrapper){
        return sqlSession().selectList("selectRewards", filterWrapper);
    }

    public Long getRewardsCount(FilterWrapper<Reward> filterWrapper){
        return sqlSession().selectOne("selectRewardsCount", filterWrapper);
    }

    public void deleteRewards(Long periodId){
        sqlSession().delete("deleteRewards", periodId);
    }

    public BigDecimal getRewardsPointSum(Long workerId, Long rewardTypeId, Long saleId, Long managerId, Long rewardStatusId, Long periodId, String filter) {
        return sqlSession().selectOne("selectRewardsPointSum", Maps.of("workerId", workerId, "rewardTypeId", rewardTypeId, "saleId", saleId,
                "managerId", managerId, "rewardStatusId", rewardStatusId, "periodId", periodId, "filter", filter));
    }

    public BigDecimal getRewardsPointSumBefore(Long workerId, Long rewardTypeId, Long saleId, Long managerId, Long rewardStatusId, Long periodId) {
        return getRewardsPointSum(workerId, rewardTypeId, saleId, managerId, rewardStatusId, periodId, "before");
    }

    public BigDecimal getRewardsPointSum(Long workerId, Long rewardTypeId, Long saleId, Long managerId, Long rewardStatusId) {
        return getRewardsPointSum(workerId, rewardTypeId, saleId, managerId, rewardStatusId, null, null);
    }

    public BigDecimal getRewardsAmountSum(Long workerId, Long rewardTypeId, Long saleId, Long managerId, Long rewardStatusId, Long periodId, String filter) {
        return sqlSession().selectOne("selectRewardsAmountSum", Maps.of("workerId", workerId, "rewardTypeId", rewardTypeId, "saleId", saleId,
                "managerId", managerId, "rewardStatusId", rewardStatusId, "periodId", periodId, "filter", filter));
    }

    public BigDecimal getRewardsAmountSum(Long workerId, Long rewardTypeId, Long saleId, Long managerId, Long rewardStatusId) {
        return getRewardsAmountSum(workerId, rewardTypeId, saleId, managerId, rewardStatusId, null, null);
    }
}
