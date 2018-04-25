package ru.complitex.user.mapper;

import org.apache.ibatis.session.SqlSession;
import ru.complitex.user.entity.User;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 17.12.2017 13:03
 */
public class UserMapper {
    @Inject
    private SqlSession sqlSession;

    public void insertUser(User user){
        sqlSession.insert("insertUser", user);
    }

    public Long getUserId(String login){
        return sqlSession.selectOne("selectUserId", login);
    }

    public User getUser(Long id){
        return sqlSession.selectOne("selectUser", id);
    }

    public User getUser(String login){
        return sqlSession.selectOne("selectUserByLogin", login);
    }
}
