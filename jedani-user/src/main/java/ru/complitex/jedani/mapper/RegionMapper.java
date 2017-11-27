package ru.complitex.jedani.mapper;

import org.apache.ibatis.session.SqlSession;
import ru.complitex.jedani.entity.Region;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 23.11.2017 17:17
 */
@RequestScoped
public class RegionMapper {
    @Inject
    private SqlSession sqlSession;

    public void insertRegion(Region region){
        sqlSession.insert("insertRegion", region);
    }

    public boolean hasRegion(Integer id){
        return sqlSession.selectOne("selectHasRegion", id);
    }
}
