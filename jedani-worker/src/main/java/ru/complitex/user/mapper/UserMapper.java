package ru.complitex.user.mapper;

import org.apache.ibatis.session.SqlSession;
import ru.complitex.user.entity.User;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 17.12.2017 13:03
 */
public class UserMapper implements Serializable {
    @Inject
    private transient SqlSession sqlSession;

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

    public void updateUserPassword(User user){
        sqlSession.update("updateUserPassword", user);
    }
}
