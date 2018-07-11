package ru.complitex.domain.service;

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
    private DomainNodeMapper domainNodeMapper;

    public void rebuildRootIndex(String entityName, Long rootObjectId, Long parentEntityAttributeId){
        domainNodeMapper.clearIndex(entityName, rootObjectId);

        DomainNode root = domainNodeMapper.getDomainNode(entityName, rootObjectId);

        root.setLeft(1L);
        root.setRight(2L);
        root.setLevel(0L);

        domainNodeMapper.update(root);

        domainNodeMapper.rebuildIndex(root, parentEntityAttributeId);
    }

    @Transactional
    public void move(DomainNode parentDomainNode, DomainNode domainNode){
        List<Long> nodeIds = domainNodeMapper.getDomainNodeIds(domainNode);

        int sign = domainNode.getLeft() - parentDomainNode.getRight() > 0 ? 1 : -1;
        long start = sign > 0 ? parentDomainNode.getRight() - 1 : domainNode.getRight();
        long stop = sign > 0 ? domainNode.getLeft() : parentDomainNode.getRight();
        long delta = (long) (nodeIds.size() * 2);

        domainNodeMapper.updateDomainNodeMove(domainNode.getEntityName(), sign, delta, start, stop);

        long nodeDelta = stop - start - 1;
        int nodeSign = sign > 0 ? -1 : 1;
        long levelMod = parentDomainNode.getLevel() + 1 - domainNode.getLevel();

        domainNodeMapper.updateDomainNodeMove(domainNode.getEntityName(), nodeIds, nodeSign, nodeDelta, levelMod);
    }
}
