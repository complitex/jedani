package ru.complitex.domain.mapper;

import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.domain.entity.DomainNode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ru.complitex.common.util.Maps.of;

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

    public List<DomainNode> getChildren(DomainNode domainNode, Long parentEntityAttributeId){
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

    public List<Long> getDomainNodeIds(DomainNode domainNode) {
        return sqlSession().selectList("selectDomainNodeIds", domainNode);
    }

    public void lockTablesWrite(String... tables){
        sqlSession().update("lockTable", Arrays.asList(tables));
    }

    public void unlockTables(){
        sqlSession().update("unlockTables");
    }

    public void updateDomainNodeMove(String entityName, Long signDelta, Long start, Long stop){
        Map<String, Object> map =  of("entityName", entityName,
                "signDelta", signDelta, "start", start, "stop", stop);

        sqlSession().update("updateDomainNodeMoveRight", map);
        sqlSession().update("updateDomainNodeMoveLeft", map);
    }

    public void updateDomainNodeMove(String entityName, List<Long> nodeIds, Long nodeSignDelta, Long levelMod){
        sqlSession().update("updateDomainNodeMove", of("entityName", entityName, "nodeIds", nodeIds,
                "nodeSignDelta", nodeSignDelta, "levelMod", levelMod));
    }

    public boolean validateDomainNodeLeftRight(String entityName){
        return sqlSession().selectOne("validateDomainNodeLeftRight", of("entityName", entityName));
    }

    public boolean validateDomainNodeMinLeft(String entityName){
        return sqlSession().selectOne("validateDomainNodeMinLeft", of("entityName", entityName));
    }

    public boolean validateDomainNodeMaxRight(String entityName){
        return sqlSession().selectOne("validateDomainNodeMaxRight", of("entityName", entityName));
    }

    public boolean validateDomainNodeDiff(String entityName){
        return sqlSession().selectOne("validateDomainNodeDiff", of("entityName", entityName));
    }

    public boolean validateDomainNodeLevel(String entityName){
        return sqlSession().selectOne("validateDomainNodeLevel", of("entityName", entityName));
    }
}
