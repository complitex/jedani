package ru.complitex.domain.mapper;

import org.apache.ibatis.session.SqlSession;
import ru.complitex.domain.entity.Domain;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 29.11.2017 17:54
 */
public class DomainMapper {
    @Inject
    private SqlSession sqlSession;

    @Inject
    private SequenceMapper sequenceMapper;

    @Inject
    private DomainMapper domainMapper;

    @Inject
    private AttributeMapper attributeMapper;

    @Inject
    private ValueMapper valueMapper;

    public void insertDomain(Domain domain){
        sqlSession.insert("insertDomain", domain);
    }

    public void save(Domain domain){
        if (domain.getObjectId() == null){
            domain.setObjectId(sequenceMapper.nextId(domain.getEntityName()));

            domainMapper.insertDomain(domain);

            domain.getAttributes().forEach(a -> {
                a.setEntityName(domain.getEntityName());
                a.setObjectId(domain.getObjectId());
                a.setStartDate(domain.getStartDate());

                attributeMapper.insertAttribute(a);

                if (a.getValues() != null){
                    a.getValues().forEach(s -> {
                        s.setEntityName(domain.getEntityName());
                        s.setAttributeId(a.getId());

                        valueMapper.insertStringValue(s);
                    });
                }
            });
        }
    }

    public boolean hasExternalId(Domain domain){
        return sqlSession.selectOne("hasExternalId", domain);
    }

    public Domain getDomain(Domain domain){
        return sqlSession.selectOne("selectDomain", domain);
    }
}
