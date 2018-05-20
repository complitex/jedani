package ru.complitex.domain.mapper;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Status;
import ru.complitex.domain.entity.Value;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 29.11.2017 17:54
 */
public class DomainMapper extends BaseMapper {
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

        sqlSession().insert("insertDomain", domain);

        domain.getAttributes().forEach(a -> {
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
                        !Objects.equals(a.getNumber(), dbAttribute.getNumber()) ||
                        !Objects.equals(a.getDate(), dbAttribute.getDate());

                if (!update){
                    if (a.getValues() != null && dbAttribute.getValues() != null){
                        update = a.getValues().stream().anyMatch(v -> {
                            if (v.getLocaleId() != null) {
                                Value dbValue = dbAttribute.getValue(v.getLocaleId());

                                return !Objects.equals(v.getText(), dbValue != null ? dbValue.getText() : null);
                            }else{
                                return dbAttribute.getValues().stream()
                                        .noneMatch(dbV -> Objects.equals(v.getText(), dbV.getText()) ||
                                                Objects.equals(v.getNumber(), dbV.getNumber()));
                            }
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

        sqlSession().update("updateDomain", domain);
    }

    public Boolean hasDomain(Domain domain){
        return sqlSession().selectOne("hasDomain", domain);
    }

    public Domain getDomain(String entityName, Long objectId){
        Domain domain = new Domain();
        domain.setEntityName(entityName);
        domain.setObjectId(objectId);

        return getDomain(domain);
    }

    public Domain getDomainByParentId(String entityName, Long parentId){
        Domain domain = new Domain();
        domain.setEntityName(entityName);
        domain.setParentId(parentId);

        return getDomain(domain);
    }

    public Domain getDomainByExternalId(String entityName, String externalId){
        Domain domain = new Domain();
        domain.setEntityName(entityName);
        domain.setExternalId(externalId);

        return getDomain(domain);
    }

    public Domain getDomain(Domain domain){
        return sqlSession().selectOne("selectDomain", domain);
    }

    public List<Domain> getDomains(FilterWrapper<? extends Domain> filterWrapper){
        return sqlSession().selectList("selectDomains", filterWrapper);
    }

    public Long getDomainsCount(FilterWrapper<? extends Domain> filterWrapper){
        return sqlSession().selectOne("selectDomainsCount", filterWrapper);
    }

    public Long getDomainObjectId(Domain domain){
        return sqlSession().selectOne("selectDomainObjectId", domain);
    }
}
