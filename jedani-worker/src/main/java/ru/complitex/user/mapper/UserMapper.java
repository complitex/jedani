package ru.complitex.user.mapper;

import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.user.entity.User;

/**
 * @author Anatoly A. Ivanov
 * 17.12.2017 13:03
 */
public class UserMapper extends BaseMapper {
    public void insertUser(User user){
        sqlSession().insert("insertUser", user);
    }

    public Long getUserId(String login){
        return sqlSession().selectOne("selectUserId", login);
    }

    public User getUser(Long id){
        return sqlSession().selectOne("selectUser", id);
    }

    public User getUser(String login){
        return sqlSession().selectOne("selectUserByLogin", login);
    }

    public void updateUserPassword(User user){
        sqlSession().update("updateUserPassword", user);
    }

    public void updateUserLogin(User user){
        sqlSession().update("updateUserLogin", user);
    }
}
