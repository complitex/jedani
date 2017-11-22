package ru.complitex.jedani.mapper;

import org.apache.ibatis.session.SqlSession;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 22.11.2017 15:23
 */
@RequestScoped
public class UserMapper {
    @Inject
    private SqlSession sqlSession;

    public String ping(){
        return sqlSession.selectOne("ping");
    }
}
