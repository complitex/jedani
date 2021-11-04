package ru.complitex.domain.service;

import org.mybatis.cdi.Transactional;
import ru.complitex.domain.entity.DomainNode;
import ru.complitex.domain.mapper.DomainNodeMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;

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
                    domainNode.setUpdateLeft(0L);
                    domainNode.setUpdateRight(0L);
                    domainNode.setUpdateLevel(0L);
                }));

        map.put(0L, List.of(root));

        root.setUpdateLeft(1L);
        root.setUpdateRight(2L);
        root.setUpdateLevel(0L);
    }

    private void rebuildIndex(DomainNode parent,  Map<Long, List<DomainNode>> map) {
        Long right = parent.getUpdateRight();
        Long level  = parent.getUpdateLevel();

        for (DomainNode domainNode : map.get(parent.getObjectId())) {
            map.values().stream()
                    .flatMap(Collection::stream)
                    .filter(n -> n.getUpdateLeft() >= right)
                    .forEach(n -> n.setUpdateLeft(n.getUpdateLeft() + 2));

            map.values().stream()
                    .flatMap(Collection::stream)
                    .filter(n -> n.getUpdateRight() >= right)
                    .forEach(n -> n.setUpdateRight(n.getUpdateRight() + 2));

            domainNode.setUpdateLeft(right);
            domainNode.setUpdateRight(right+ 1);
            domainNode.setUpdateLevel(level + 1);

            rebuildIndex(domainNode, map);
        }
    }

    private void update(DomainNode domainNode) {
        boolean update = false;

        if (!Objects.equals(domainNode.getLeft(), domainNode.getUpdateLeft())) {
            domainNode.setLeft(domainNode.getUpdateLeft());

            update = true;
        }

        if (!Objects.equals(domainNode.getRight(), domainNode.getUpdateRight())) {
            domainNode.setRight(domainNode.getUpdateRight());

            update = true;
        }

        if (!Objects.equals(domainNode.getLevel(), domainNode.getUpdateLevel())) {
            domainNode.setLevel(domainNode.getUpdateLevel());

            update = true;
        }

        if (update) {
            domainNodeMapper.update(domainNode);
        }
    }

    private void updateIndex(DomainNode root, Map<Long, List<DomainNode>> map) {
        update(root);

        map.values().stream()
                .flatMap(Collection::stream)
                .forEach(this::update);
    }
}
