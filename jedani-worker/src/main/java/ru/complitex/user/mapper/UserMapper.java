package ru.complitex.user.mapper;

import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.user.entity.User;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 17.12.2017 13:03
 */
public class UserMapper extends BaseMapper {
    @Inject
    private UserGroupMapper userGroupMapper;

    public void insert(User user){
        sqlSession().insert("insertUser", user);

        if (user.getUserGroups() != null) {
            user.getUserGroups().forEach(userGroupMapper::insertUserGroup);
        }
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
        User dbUser = getUser(user.getId());
        userGroupMapper.deleteUserGroups(dbUser.getLogin());

        sqlSession().update("updateUserLogin", user);

        if (user.getUserGroups() != null) {
            user.getUserGroups().forEach(userGroupMapper::insertUserGroup);
        }
    }

    public void updateUserGroups(User user){
        User dbUser = getUser(user.getId());

        dbUser.getUserGroups().stream()
                .filter(ug -> !user.hasRole(ug.getName()))
                .forEach(ug -> userGroupMapper.deleteUserGroup(ug.getId()));

        user.getUserGroups().stream()
                .filter(ug -> !ug.getName().isEmpty() && !dbUser.hasRole(ug.getName()))
                .forEach(ug -> userGroupMapper.insertUserGroup(ug));
    }

    public void deleteUser(User user){
        if (user.getId() != null) {
            userGroupMapper.deleteUserGroupsByUserId(user.getId());

            sqlSession().delete("deleteUser", user.getId());

            user.setId(null);
        }
    }
}
