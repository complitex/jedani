package ru.complitex.jedani.worker.mapper;

import org.mybatis.cdi.Transactional;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.jedani.worker.entity.Reward;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 20.11.2019 5:25 PM
 */
public class RewardMapper extends BaseMapper {
    @Transactional
    public void deleteRewards(Long periodId){
        List<Long> ids = sqlSession().selectList("selectRewardIdsByPeriod", periodId);

        if (!ids.isEmpty()) {
            sqlSession().delete("deleteRewardAttributesByDomainIds", ids);
            sqlSession().delete("deleteRewardsByIds", ids);
        }
    }

    public List<Reward> getRewards(FilterWrapper<Reward> filterWrapper){
        return sqlSession().selectList("selectRewards", filterWrapper);
    }

    public Long getRewardsCount(FilterWrapper<Reward> filterWrapper){
        return sqlSession().selectOne("selectRewardsCount", filterWrapper);
    }
}
