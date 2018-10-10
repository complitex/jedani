package ru.complitex.user.mapper;

import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.user.entity.UserGroup;

/**
 * @author Anatoly A. Ivanov
 * 17.12.2017 13:04
 */
public class UserGroupMapper extends BaseMapper {

    public void insertUserGroup(UserGroup userGroup){
        sqlSession().insert("insertUserGroup", userGroup);
    }

    public void deleteUserGroups(String login){
        sqlSession().delete("deleteUserGroups", login);
    }

    public void deleteUserGroupsByUserId(Long userId){
        sqlSession().delete("deleteUserGroupsByUserId", userId);
    }

    public void deleteUserGroup(Long userGroupId){
        sqlSession().delete("deleteUserGroup", userGroupId);
    }
}
