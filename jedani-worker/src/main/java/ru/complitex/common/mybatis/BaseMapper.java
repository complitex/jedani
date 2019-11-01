package ru.complitex.common.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.cdi.NonContextual;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 07.05.2018 0:18
 */
public abstract class BaseMapper implements Serializable {
    @Inject
    private transient SqlSession sqlSession;

    public SqlSession sqlSession() {
        if (sqlSession == null){
            NonContextual.of(this).inject(this);
        }

        return sqlSession;
    }
}
