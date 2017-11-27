package ru.complitex.jedani.mapper;

import org.apache.ibatis.session.SqlSession;
import ru.complitex.jedani.entity.User;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 22.11.2017 15:23
 */
public class UserMapper {
    @Inject
    private SqlSession sqlSession;

    public String ping(){
        return sqlSession.selectOne("ping");
    }

    public void insertUser(User user){
        sqlSession.insert("insertUser", user);
    }

    public boolean hasUser(Integer id){
        return sqlSession.selectOne("hasUser", id);
    }
}
