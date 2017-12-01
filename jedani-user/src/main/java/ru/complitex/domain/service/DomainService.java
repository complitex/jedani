package ru.complitex.domain.service;

import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.mapper.AttributeMapper;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.mapper.SequenceMapper;
import ru.complitex.domain.mapper.StringValueMapper;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 29.11.2017 17:54
 */
public class DomainService {
    @Inject
    private SequenceMapper sequenceMapper;

    @Inject
    private DomainMapper domainMapper;

    @Inject
    private AttributeMapper attributeMapper;

    @Inject
    private StringValueMapper stringValueMapper;

    public void save(Domain domain){
        if (domain.getObjectId() == null){
            domain.setObjectId(sequenceMapper.nextId(domain.getEntityName()));

            domainMapper.insertDomain(domain);

            domain.getAttributes().forEach(a -> {
                a.setObjectId(domain.getObjectId());
                a.setStartDate(domain.getStartDate());

                if (!a.getStringValues().isEmpty()){
                    Long valueId = sequenceMapper.nextId(domain.getEntityName() + "_string_value");

                    a.setValueId(valueId);

                    a.getStringValues().forEach(s -> {
                        s.setId(valueId);

                        stringValueMapper.insertStringValue(s);
                    });
                }

                //todo string value ref to attribute id, dev attribute pair





            });
        }
    }


}
