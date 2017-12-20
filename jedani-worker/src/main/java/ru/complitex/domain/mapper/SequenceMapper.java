package ru.complitex.domain.mapper;

import org.apache.ibatis.session.SqlSession;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 16:27
 */
public class SequenceMapper {
    @Inject
    private SqlSession sqlSession;

    public Long nextId(String name){
        Long nextId = sqlSession.selectOne("selectSequenceId", name);
        sqlSession.update("incrementSequenceId", name);

        return nextId;
    }
}
