package ru.complitex.domain.mapper;

import org.mybatis.cdi.Transactional;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Status;
import ru.complitex.domain.entity.Value;

import javax.inject.Inject;
import java.security.Principal;
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

    @Inject
    private Principal principal;

    @Transactional
    public void insertDomain(Domain<?> domain){
        domain.setId(null);
        domain.setObjectId(-1L);

        if (domain.getStartDate() == null){
            domain.setStartDate(new Date());
        }
        if (domain.getStatus() == null){
            domain.setStatus(Status.ACTIVE);
        }

        sqlSession().insert("insertDomain", domain);

        domain.setObjectId(domain.getId());
        sqlSession().update("updateDomainObjectId", domain);

        domain.getAttributes().forEach(a -> {
            a.setEntityName(domain.getEntityName());
            a.setDomainId(domain.getId());
            a.setUserId(domain.getUserId());

            attributeMapper.insertAttribute(a, domain.getStartDate());
        });
    }

    public void updateDomain(Domain<?> domain){
        Date date = new Date();

        Domain<?> dbDomain = getDomain(domain.getEntityName(), domain.getObjectId(),
                domain.isUseDateAttribute(), domain.isUseNumberValue());

        domain.getAttributes().forEach(a -> {
            a.setEntityName(domain.getEntityName());
            a.setDomainId(domain.getId());
            a.setUserId(domain.getUserId());

            Attribute dbAttribute = dbDomain.getAttribute(a.getEntityAttributeId());

            if (dbAttribute == null){
                attributeMapper.insertAttribute(a, date);
            }else{
                boolean update = !Objects.equals(a.getText(), dbAttribute.getText()) ||
                        !Objects.equals(a.getNumber(), dbAttribute.getNumber()) ||
                        !Objects.equals(a.getDate(), dbAttribute.getDate());

                if (!update){
                    if (a.getValues() != null){
                        if (dbAttribute.getValues() != null) {
                            boolean count = a.getValues().stream().filter(v -> v.getLocaleId() == null).count() ==
                                    dbAttribute.getValues().stream().filter(v -> v.getLocaleId() == null).count();

                            update =  !count ||
                                    a.getValues().stream().anyMatch(v -> {
                                        if (v.getLocaleId() != null) {
                                            Value dbValue = dbAttribute.getValue(v.getLocaleId());

                                            return !Objects.equals(v.getText(), dbValue != null ? dbValue.getText() : null);
                                        }else{
                                            return dbAttribute.getValues().stream()
                                                    .noneMatch(dbV -> Objects.equals(v.getText(), dbV.getText()) &&
                                                            Objects.equals(v.getNumber(), dbV.getNumber()));
                                        }
                                    });
                        }else {
                            update = true;
                        }
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

    public Boolean hasDomain(String entityName, Long entityAttributeId, String text){
        Domain domain = new Domain();
        domain.setEntityName(entityName);
        domain.setText(entityAttributeId, text);

        return sqlSession().selectOne("hasDomain", domain);
    }

    public Domain getDomain(String entityName, Long objectId, boolean useDateAttribute, boolean useNumberValue){
        if (objectId == null){
            return null;
        }

        Domain domain = new Domain();
        domain.setEntityName(entityName);
        domain.setObjectId(objectId);
        domain.setUseDateAttribute(useDateAttribute);
        domain.setUseNumberValue(useNumberValue);

        return getDomain(domain);
    }

    public Domain getDomain(String entityName, Long objectId){
        return getDomain(entityName, objectId, false, false);
    }

    public Domain getDomainByParentId(String entityName, Long parentId){
        Domain domain = new Domain();
        domain.setEntityName(entityName);
        domain.setParentId(parentId);

        return getDomain(domain);
    }

    public Domain getDomain(String entityName, Long entityAttributeId, String text){
        Domain domain = new Domain();
        domain.setEntityName(entityName);
        domain.setText(entityAttributeId, text);

        return getDomain(domain);
    }

    public Domain getDomain(String entityName, Long entityAttributeId, Long number){
        Domain domain = new Domain();
        domain.setEntityName(entityName);
        domain.setNumber(entityAttributeId, number);

        return getDomain(domain);
    }

    private Domain getDomain(Domain domain){
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
