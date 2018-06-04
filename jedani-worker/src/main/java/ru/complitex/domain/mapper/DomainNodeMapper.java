package ru.complitex.domain.mapper;

import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.domain.entity.DomainNode;

/**
 * @author Anatoly A. Ivanov
 * 04.06.2018 12:18
 */
public class DomainNodeMapper extends BaseMapper {
    public void clearDomainNodeIndex(DomainNode domainNode){
        sqlSession().update("clearDomainNodeIndex", domainNode);
    }


}
