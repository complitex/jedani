package ru.complitex.domain.service;

import ru.complitex.domain.entity.DomainNode;
import ru.complitex.domain.mapper.DomainNodeMapper;

/**
 * @author Anatoly A. Ivanov
 * 05.06.2018 15:37
 */
public class DomainNodeService {
    private DomainNodeMapper domainNodeMapper;

    public void rebuildNodeIndex(DomainNode domainNode){
        domainNodeMapper.clearDomainNodeIndex(domainNode);

    }
}
