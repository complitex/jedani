package ru.complitex.domain.service;

import org.apache.wicket.cdi.NonContextual;
import org.mybatis.cdi.Transactional;
import ru.complitex.domain.entity.DomainNode;
import ru.complitex.domain.mapper.DomainNodeMapper;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 05.06.2018 15:37
 */
public class DomainNodeService implements Serializable {
    @Inject
    private transient DomainNodeMapper domainNodeMapper;

    private DomainNodeMapper getDomainNodeMapper(){
        if (domainNodeMapper == null){
            NonContextual.of(DomainNodeService.class).inject(this);
        }

        return domainNodeMapper;
    }

    @Transactional
    public void rebuildRootIndex(String entityName, Long rootObjectId, Long parentEntityAttributeId){
        try {
            getDomainNodeMapper().lockTablesWrite(entityName, entityName + " as d", entityName + "_attribute as a");

            getDomainNodeMapper().clearIndex(entityName, rootObjectId);

            DomainNode root = getDomainNodeMapper().getDomainNode(entityName, rootObjectId);

            root.setLeft(1L);
            root.setRight(2L);
            root.setLevel(0L);

            getDomainNodeMapper().update(root);

            getDomainNodeMapper().rebuildIndex(root, parentEntityAttributeId);

            if (!validate(entityName)){
                throw new RuntimeException("Index is not validated");
            }
        } finally {
            getDomainNodeMapper().unlockTables();
        }
    }

    @Transactional
    public void move(DomainNode parentDomainNode, DomainNode domainNode){
        if (domainNode.getId().equals(parentDomainNode.getId()) || (domainNode.getLeft() < parentDomainNode.getLeft() &&
                domainNode.getRight() > parentDomainNode.getRight())) {
            throw new RuntimeException("Node cannot move to it's child");
        }

        List<Long> nodeIds = getDomainNodeMapper().getDomainNodeIds(domainNode);

        int sign = (domainNode.getLeft() - parentDomainNode.getRight()) > 0 ? 1 : -1;
        long start = sign > 0 ? parentDomainNode.getRight() - 1 : domainNode.getRight();
        long stop = sign > 0 ? domainNode.getLeft() : parentDomainNode.getRight();
        long delta = (long) (nodeIds.size() * 2);

        try {
            getDomainNodeMapper().lockTablesWrite(domainNode.getEntityName());

            getDomainNodeMapper().updateDomainNodeMove(domainNode.getEntityName(), sign*delta, start, stop);

            long nodeDelta = stop - start - 1;
            int nodeSign = sign > 0 ? -1 : 1;
            long levelMod = parentDomainNode.getLevel() + 1 - domainNode.getLevel();

            getDomainNodeMapper().updateDomainNodeMove(domainNode.getEntityName(), nodeIds, nodeSign*nodeDelta, levelMod);

            if (!validate(domainNode.getEntityName())){
                throw new RuntimeException("Index is not validated");
            }
        } finally {
            getDomainNodeMapper().unlockTables();
        }
    }

    public boolean validate(String entityName){
        return getDomainNodeMapper().validateDomainNodeLeftRight(entityName) &&
                getDomainNodeMapper().validateDomainNodeMinLeft(entityName) &&
                getDomainNodeMapper().validateDomainNodeMaxRight(entityName) &&
                getDomainNodeMapper().validateDomainNodeDiff(entityName) &&
                getDomainNodeMapper().validateDomainNodeLevel(entityName);
    }
}
