package ru.complitex.jedani.worker.mapper;

import ru.complitex.common.mybatis.BaseMapper;

/**
 * @author Ivanov Anatoliy
 */
public class RewardNodeMapper extends BaseMapper {
    public void deleteRewardNodes(Long periodId) {
        sqlSession().delete("deleteRewardNodes", periodId);
    }
}
