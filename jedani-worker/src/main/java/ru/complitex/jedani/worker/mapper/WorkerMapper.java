package ru.complitex.jedani.worker.mapper;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.common.util.MapUtil;
import ru.complitex.jedani.worker.entity.Worker;

import java.util.Collections;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 9:22
 */
public class WorkerMapper extends BaseMapper {
    public String getNewJId(){
        String maxJId = sqlSession().selectOne("selectMaxJId");

        try {
            return maxJId != null ? StringUtils.leftPad(Long.parseLong(maxJId) + 1 + "", 6, '0') : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean isExistJId(Long objectId, String jId){
        return sqlSession().selectOne("selectIsExistJId", MapUtil.of("objectId", objectId, "jId", jId));
    }

    public Worker getWorker(Long objectId){
        if (objectId == null){
            return null;
        }

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

    public List<Worker> getWorkers(String s){
        if (Strings.isNullOrEmpty(s)){
            return Collections.emptyList();
        }

        String[] array = s.split("\\s");

        if (array.length == 0){
            return Collections.emptyList();
        }

        return sqlSession().selectList("selectWorkersByString", array);
    }

    public Long getWorkerLevelDepth(Long objectId){
        return sqlSession().selectOne("selectWorkerLevelDepth", objectId);
    }


}

