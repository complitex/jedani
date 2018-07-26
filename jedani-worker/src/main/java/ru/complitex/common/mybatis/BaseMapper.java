package ru.complitex.common.mybatis;

import org.apache.ibatis.session.SqlSessionManager;
import org.apache.wicket.cdi.NonContextual;
import org.mybatis.cdi.SqlSessionManagerRegistry;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 07.05.2018 0:18
 */
public abstract class BaseMapper implements Serializable {
    @Inject
    private transient SqlSessionManagerRegistry sqlSessionManagerRegistry;

    private transient SqlSessionManager sqlSessionManager;

    private SqlSessionManagerRegistry getSqlSessionManagerRegistry(){
        if (sqlSessionManagerRegistry == null){
            NonContextual.of(BaseMapper.class).inject(this);
        }

        return sqlSessionManagerRegistry;
    }

    public SqlSessionManager sqlSession(){
        if (sqlSessionManager == null){
            sqlSessionManager = getSqlSessionManagerRegistry().getManagers().iterator().next();
        }

        return sqlSessionManager;
    }
}
