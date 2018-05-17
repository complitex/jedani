package ru.complitex.jedani.worker.mapper;

import org.apache.commons.lang3.StringUtils;
import ru.complitex.common.mybatis.BaseMapper;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 9:22
 */
public class WorkerMapper extends BaseMapper {
    public String getNewJId(){
        return StringUtils.leftPad(Long.parseLong(sqlSession().selectOne("selectMaxJId")) + 1 + "", 6, '0');
    }

    public boolean isExistJId(String jid){
        return sqlSession().selectOne("selectIsExistJId", jid);
    }

    public int getSubWorkersCount(Long objectId){
        return sqlSession().selectOne("selectSubWorkersCount", objectId);
    }
}

