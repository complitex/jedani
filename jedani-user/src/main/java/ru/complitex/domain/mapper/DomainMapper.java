package ru.complitex.domain.mapper;

import org.apache.ibatis.session.SqlSession;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Status;

import javax.inject.Inject;
import java.util.Date;

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
    private AttributeMapper attributeMapper;

    @Inject
    private ValueMapper valueMapper;

    public void insertDomain(Domain domain){
        sqlSession.insert("insertDomain", domain);
    }

    public void save(Domain domain){
        if (domain.getObjectId() == null){
            domain.setObjectId(sequenceMapper.nextId(domain.getEntityName()));

            if (domain.getStartDate() == null){
                domain.setStartDate(new Date());
            }
            if (domain.getStatus() == null){
                domain.setStatus(Status.ACTIVE);
            }

            insertDomain(domain);

            domain.getAttributes().forEach(a -> {
                a.setEntityName(domain.getEntityName());
                a.setObjectId(domain.getObjectId());
                a.setStartDate(domain.getStartDate());

                if (a.getStatus() == null){
                    a.setStatus(Status.ACTIVE);
                }

                attributeMapper.insertAttribute(a);

                if (a.getValues() != null){
                    a.getValues().stream().filter(s -> s.getText() != null)
                            .forEach(s -> {
                                s.setEntityName(domain.getEntityName());
                                s.setAttributeId(a.getId());

                                valueMapper.insertValue(s);
                            });
                }
            });
        }
    }

    public Boolean hasExternalId(Domain domain){
        return sqlSession.selectOne("hasExternalId", domain);
    }

    public Domain getDomain(Domain domain){
        return sqlSession.selectOne("selectDomain", domain);
    }
}
