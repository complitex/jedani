package ru.complitex.domain.mapper;

import org.apache.ibatis.session.SqlSession;
import ru.complitex.domain.entity.Value;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 01.12.2017 15:42
 */
public class ValueMapper implements Serializable {
    @Inject
    private transient SqlSession sqlSession;

    public void insertValue(Value value){
        sqlSession.insert("insertValue", value);
    }
}
