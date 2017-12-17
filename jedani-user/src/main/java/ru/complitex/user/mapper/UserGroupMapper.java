package ru.complitex.user.mapper;

import org.apache.ibatis.session.SqlSession;
import ru.complitex.user.entity.UserGroup;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 17.12.2017 13:04
 */
public class UserGroupMapper {
    @Inject
    private SqlSession sqlSession;

    private void insertUserGroup(UserGroup userGroup){
        sqlSession.insert("insertUserGroup", userGroup);
    }


}
