package ru.complitex.jedani.mapper;

import org.apache.ibatis.session.SqlSession;
import ru.complitex.jedani.entity.City;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 23.11.2017 17:17
 */
public class CityMapper {
    @Inject
    private SqlSession sqlSession;

    public void insertCity(City city){
        sqlSession.insert("insertCity", city);
    }

    public boolean hasCity(Integer id){
        return sqlSession.selectOne("hasCity", id);
    }

}
