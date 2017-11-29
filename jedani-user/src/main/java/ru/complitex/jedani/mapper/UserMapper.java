package ru.complitex.jedani.mapper;

import org.apache.ibatis.session.SqlSession;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.jedani.entity.User;

import javax.inject.Inject;
import java.util.List;

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

    public List<User> getUserList(FilterWrapper<User> filterWrapper){
        return sqlSession.selectList("selectUserList", filterWrapper);
    }

    public Integer getUserListCount(FilterWrapper<User> filterWrapper){
        return sqlSession.selectOne("selectUserListCount", filterWrapper);
    }

    public User getUser(Integer id){
        return sqlSession.selectOne("selectUser", id);
    }
}
