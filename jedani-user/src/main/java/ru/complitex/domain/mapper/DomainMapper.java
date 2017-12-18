package ru.complitex.domain.mapper;

import org.apache.ibatis.session.SqlSession;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Status;
import ru.complitex.domain.entity.Value;

import javax.inject.Inject;
import java.util.Date;
import java.util.Objects;

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

    public void insertDomain(Domain domain){
        domain.setObjectId(sequenceMapper.nextId(domain.getEntityName()));

        if (domain.getStartDate() == null){
            domain.setStartDate(new Date());
        }
        if (domain.getStatus() == null){
            domain.setStatus(Status.ACTIVE);
        }

        sqlSession.insert("insertDomain", domain);

        domain.getAttributes().stream()
                .filter(a -> (a.getText() != null && !a.getText().isEmpty()) || a.getNumber() != null || a.getValues() != null)
                .forEach(a -> {
                    a.setEntityName(domain.getEntityName());
                    a.setObjectId(domain.getObjectId());

                    attributeMapper.insertAttribute(a, domain.getStartDate());
                });
    }

    public void updateDomain(Domain domain){
        Date date = new Date();

        Domain dbDomain = getDomain(domain);

        domain.getAttributes().forEach(a -> {
            a.setEntityName(domain.getEntityName());
            a.setObjectId(domain.getObjectId());

            Attribute dbAttribute = dbDomain.getAttribute(a.getEntityAttributeId());

            if (dbAttribute == null){
                attributeMapper.insertAttribute(a, date);
            }else{
                boolean update = !Objects.equals(a.getText(), dbAttribute.getText()) ||
                        !Objects.equals(a.getNumber(), dbAttribute.getNumber());

                if (!update){
                    if (a.getValues() != null && dbAttribute.getValues() != null){
                        update = a.getValues().stream().anyMatch(v -> {
                            Value dbValue = dbAttribute.getValue(v.getLocaleId());

                            return !Objects.equals(v.getText(), dbValue != null ? dbValue.getText() : null);
                        });
                    }
                }

                if (update){
                    attributeMapper.archiveAttribute(dbAttribute, date);

                    attributeMapper.insertAttribute(a, date);
                }
            }
        });

        dbDomain.getAttributes().forEach(a -> {
            if (domain.getAttribute(a.getEntityAttributeId()) == null){
                attributeMapper.archiveAttribute(a, date);
            }
        });
    }

    public Boolean hasDomain(Domain domain){
        return sqlSession.selectOne("hasDomain", domain);
    }

    public Domain getDomain(String entityName, Long objectId){
        Domain domain = new Domain();
        domain.setEntityName(entityName);
        domain.setObjectId(objectId);

        return getDomain(domain);
    }

    public Domain getDomain(Domain domain){
        return sqlSession.selectOne("selectDomain", domain);
    }
}
