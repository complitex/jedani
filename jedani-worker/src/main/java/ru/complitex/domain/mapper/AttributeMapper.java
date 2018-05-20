package ru.complitex.domain.mapper;

import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Status;

import javax.inject.Inject;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 16:17
 */
public class AttributeMapper extends BaseMapper {
    @Inject
    private transient ValueMapper valueMapper;

    public void insertAttribute(Attribute attribute, Date startDate){
        attribute.setStartDate(startDate);

        if (attribute.getStatus() == null){
            attribute.setStatus(Status.ACTIVE);
        }
        if (attribute.getDate() != null){
            sqlSession().insert("insertAttributeWithDate", attribute);
        }else{
            sqlSession().insert("insertAttribute", attribute);
        }

        if (attribute.getValues() != null){
            attribute.getValues().stream()
                    .filter(v -> v.getText() != null || v.getNumber() != null)
                    .forEach(s -> {
                        s.setEntityName(attribute.getEntityName());
                        s.setAttributeId(attribute.getId());

                        valueMapper.insertValue(s);
                    });
        }
    }

    public void archiveAttribute(Attribute attribute, Date endDate){
        attribute.setEndDate(endDate);

        sqlSession().update("archiveAttribute", attribute);
    }
}
