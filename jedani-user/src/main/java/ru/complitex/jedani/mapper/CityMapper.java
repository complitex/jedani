package ru.complitex.jedani.mapper;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.cdi.Transactional;
import ru.complitex.jedani.entity.City;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 23.11.2017 17:17
 */
@RequestScoped
public class CityMapper {
    @Inject
    private SqlSession sqlSession;

    public void insertCity(City city){
        sqlSession.insert("insertCity", city);
    }

    @Transactional
    public void insertCities(List<City> cities){
        cities.forEach(this::insertCity);
    }
}
