package ru.complitex.domain.service;

import ru.complitex.domain.entity.DomainNode;
import ru.complitex.domain.mapper.DomainNodeMapper;

import javax.inject.Inject;
import java.io.Serializable;

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
}
