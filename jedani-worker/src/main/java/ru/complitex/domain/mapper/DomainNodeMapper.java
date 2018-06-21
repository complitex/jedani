package ru.complitex.domain.mapper;

import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.domain.entity.DomainNode;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 04.06.2018 12:18
 */
public class DomainNodeMapper extends BaseMapper {
    public DomainNode getDomainNode(String entityName, Long objectId){
        return sqlSession().selectOne("selectDomainNode", new DomainNode(entityName, objectId));
    }

    public void update(DomainNode domainNode){
        sqlSession().update("updateDomainNode", domainNode);
    }

    public void clearIndex(String entityName, Long objectId){
        sqlSession().update("clearDomainNodeIndex", new DomainNode(entityName, objectId));
    }

    private List<DomainNode> getChildren(DomainNode domainNode, Long parentEntityAttributeId){
        domainNode.getMap().put("parentEntityAttributeId", parentEntityAttributeId);

        return sqlSession().selectList("selectDomainNodeChildren", domainNode);
    }

    public void rebuildIndex(DomainNode parent, Long parentEntityAttributeId){
        for (DomainNode domainNode : getChildren(parent, parentEntityAttributeId)){
            updateIndex(getDomainNode(parent.getEntityName(), parent.getObjectId()), domainNode);

            rebuildIndex(domainNode, parentEntityAttributeId);
        }
    }

    public void updateIndex(DomainNode parent, DomainNode domainNode){
        sqlSession().update("updateDomainNodeLeft", parent);
        sqlSession().update("updateDomainNodeRight", parent);

        domainNode.setLeft(parent.getRight());
        domainNode.setRight(parent.getRight() + 1);
        domainNode.setLevel(parent.getLevel() + 1);

        update(domainNode);
    }
}