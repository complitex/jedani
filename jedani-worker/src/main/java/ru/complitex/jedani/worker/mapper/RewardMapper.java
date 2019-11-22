package ru.complitex.jedani.worker.mapper;

import org.mybatis.cdi.Transactional;
import ru.complitex.common.mybatis.BaseMapper;

import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 20.11.2019 5:25 PM
 */
public class RewardMapper extends BaseMapper {
    @Transactional
    public void deleteRewards(Date month){
        List<Long> ids = sqlSession().selectList("selectRewardIdsMyMonth", month);

        if (!ids.isEmpty()) {
            sqlSession().delete("deleteRewardAttributesByDomainIds", ids);
            sqlSession().delete("deleteRewardsByIds", ids);
        }
    }
}
