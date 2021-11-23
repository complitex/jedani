package ru.complitex.jedani.worker.mapper;

import org.mybatis.cdi.Transactional;
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

    @Transactional
    public void deleteRewards(Long periodId){
        List<Long> ids = sqlSession().selectList("selectRewardIdsByPeriod", periodId);

        if (!ids.isEmpty()) {
            sqlSession().delete("deleteRewardAttributesByDomainIds", ids);
            sqlSession().delete("deleteRewardsByIds", ids);
        }
    }

    public BigDecimal getRewardsPointSum(Long rewardTypeId, Long saleId, Long managerId, Long rewardStatusId, Long periodId, String filter) {
        return sqlSession().selectOne("selectRewardsPointSum", Maps.of("rewardTypeId", rewardTypeId, "saleId", saleId,
                "managerId", managerId, "rewardStatusId", rewardStatusId, "periodId", periodId, "filter", filter));
    }
}
