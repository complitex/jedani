package ru.complitex.jedani.worker.mapper;

import org.apache.commons.lang3.StringUtils;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.jedani.worker.entity.Worker;

import java.util.List;

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

    public Worker getWorker(Long objectId){
        return sqlSession().selectOne("selectWorker", new Worker(objectId));
    }

    public Worker getWorkerByUserId(Long userId){
        Worker worker = new Worker();
        worker.setParentId(userId);

        return sqlSession().selectOne("selectWorker", worker);
    }

    public List<Worker> getWorkers(FilterWrapper<Worker> filterWrapper){
        return sqlSession().selectList("selectWorkers", filterWrapper);
    }

    public Long getWorkersCount(FilterWrapper<Worker> filterWrapper){
        return sqlSession().selectOne("selectWorkersCount", filterWrapper);
    }
}

