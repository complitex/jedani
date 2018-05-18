package ru.complitex.domain.mapper;

import ru.complitex.common.mybatis.BaseMapper;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 16:27
 */
public class SequenceMapper extends BaseMapper {
    public Long nextId(String name){
        Long nextId = sqlSession().selectOne("selectSequenceId", name);
        sqlSession().update("incrementSequenceId", name);

        return nextId;
    }
}
