package ru.complitex.domain.service;

import org.mybatis.cdi.Transactional;
import ru.complitex.domain.entity.DomainNode;
import ru.complitex.domain.mapper.DomainNodeMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov
 * 05.06.2018 15:37
 */
@ApplicationScoped
public class DomainNodeService implements Serializable {
    @Inject
    private DomainNodeMapper domainNodeMapper;

    @Transactional
    public void rebuildRootIndex(String entityName, Long rootObjectId, Long parentEntityAttributeId){
        try {
            domainNodeMapper.lockTablesWrite(entityName, entityName + " as d", entityName + "_attribute as a");

            domainNodeMapper.clearIndex(entityName, rootObjectId);

            DomainNode root = domainNodeMapper.getDomainNode(entityName, rootObjectId);

            root.setLeft(1L);
            root.setRight(2L);
            root.setLevel(0L);

            domainNodeMapper.update(root);

            domainNodeMapper.rebuildIndex(root, parentEntityAttributeId);

            if (!validate(entityName)){
                throw new RuntimeException("Index is not validated");
            }
        } finally {
            domainNodeMapper.unlockTables();
        }
    }

    @Transactional
    public void move(DomainNode parentDomainNode, DomainNode domainNode){
        if (domainNode.getId().equals(parentDomainNode.getId()) || (domainNode.getLeft() < parentDomainNode.getLeft() &&
                domainNode.getRight() > parentDomainNode.getRight())) {
            throw new RuntimeException("Node cannot move to it's child");
        }

        List<Long> nodeIds = domainNodeMapper.getDomainNodeIds(domainNode);

        int sign = (domainNode.getLeft() - parentDomainNode.getRight()) > 0 ? 1 : -1;
        long start = sign > 0 ? parentDomainNode.getRight() - 1 : domainNode.getRight();
        long stop = sign > 0 ? domainNode.getLeft() : parentDomainNode.getRight();
        long delta = nodeIds.size() * 2L;

        try {
            domainNodeMapper.lockTablesWrite(domainNode.getEntityName());

            domainNodeMapper.updateDomainNodeMove(domainNode.getEntityName(), sign*delta, start, stop);

            long nodeDelta = stop - start - 1;
            int nodeSign = sign > 0 ? -1 : 1;
            long levelMod = parentDomainNode.getLevel() + 1 - domainNode.getLevel();

            domainNodeMapper.updateDomainNodeMove(domainNode.getEntityName(), nodeIds, nodeSign*nodeDelta, levelMod);

            if (!validate(domainNode.getEntityName())){
                throw new RuntimeException("Index is not validated");
            }
        } finally {
            domainNodeMapper.unlockTables();
        }
    }

    public boolean validate(String entityName){
        return domainNodeMapper.validateDomainNodeLeftRight(entityName) &&
                domainNodeMapper.validateDomainNodeMinLeft(entityName) &&
                domainNodeMapper.validateDomainNodeMaxRight(entityName) &&
                domainNodeMapper.validateDomainNodeDiff(entityName) &&
                domainNodeMapper.validateDomainNodeLevel(entityName);
    }

    @Transactional
    public void updateIndex(DomainNode parent, DomainNode domainNode){
        try {
            domainNodeMapper.lockTablesWrite(domainNode.getEntityName());

            domainNodeMapper.updateIndex(parent, domainNode);

            if (!validate(domainNode.getEntityName())){
                throw new RuntimeException("Index is not validated");
            }
        } finally {
            domainNodeMapper.unlockTables();
        }
    }

    @Transactional
    public void rebuildIndex(String entityName, Long rootObjectId, Long parentEntityAttributeId) {
        try {
            domainNodeMapper.lockTablesWrite(entityName, entityName + " as d", entityName + "_attribute as a");

            DomainNode root = domainNodeMapper.getDomainNode(entityName, rootObjectId);

            Map<Long, List<DomainNode>> map = new LinkedHashMap<>();

            loadIndex(root, parentEntityAttributeId, map);

            clearIndex(root, map);

            rebuildIndex(root, map);

            updateIndex(root, map);
        } finally {
            domainNodeMapper.unlockTables();
        }

        if (!validate(entityName)){
            throw new RuntimeException("Index is not validated");
        }
    }

    private void loadIndex(DomainNode parent, Long parentEntityAttributeId, Map<Long, List<DomainNode>> map) {
        List<DomainNode> domainNodes = domainNodeMapper.getChildren(parent, parentEntityAttributeId);

        map.put(parent.getObjectId(), domainNodes);

        for (DomainNode domainNode :  domainNodes) {
            loadIndex(domainNode, parentEntityAttributeId, map);
        }
    }

    private void clearIndex(DomainNode root, Map<Long, List<DomainNode>> map) {
        map.values().forEach(domainNodes  ->
                domainNodes.forEach(domainNode -> {
                    domainNode.setLeft(0L);
                    domainNode.setRight(0L);
                    domainNode.setLevel(0L);
                }));

        map.put(0L, List.of(root));

        root.setLeft(1L);
        root.setRight(2L);
        root.setLevel(0L);
    }

    private void rebuildIndex(DomainNode parent,  Map<Long, List<DomainNode>> map) {
        Long right = parent.getRight();
        Long level  = parent.getLevel();

        for (DomainNode domainNode : map.get(parent.getObjectId())) {
            map.values().stream()
                    .flatMap(Collection::stream)
                    .filter(n -> n.getLeft() >= right)
                    .forEach(n -> n.setLeft(n.getLeft() + 2));

            map.values().stream()
                    .flatMap(Collection::stream)
                    .filter(n -> n.getRight() >= right)
                    .forEach(n -> n.setRight(n.getRight() + 2));

            domainNode.setLeft(right);
            domainNode.setRight(right+ 1);
            domainNode.setLevel(level + 1);

            rebuildIndex(domainNode, map);
        }
    }

    private void updateIndex(DomainNode root, Map<Long, List<DomainNode>> map) {
        domainNodeMapper.update(root);

        map.values().stream()
                .flatMap(Collection::stream)
                .forEach(n -> domainNodeMapper.update(n));
    }
}
