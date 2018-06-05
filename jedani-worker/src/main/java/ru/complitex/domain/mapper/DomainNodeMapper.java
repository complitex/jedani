package ru.complitex.domain.mapper;

import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.domain.entity.DomainNode;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 04.06.2018 12:18
 */
public class DomainNodeMapper extends BaseMapper {
    public void clearIndex(DomainNode domainNode){
        sqlSession().update("clearDomainNodeIndex", domainNode);
    }

    public List<DomainNode> getChildren(DomainNode domainNode){
        return sqlSession().selectList("selectDomainNodeChildren", domainNode);
    }


}
