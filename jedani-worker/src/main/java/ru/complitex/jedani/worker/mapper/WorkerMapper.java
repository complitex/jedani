package ru.complitex.jedani.worker.mapper;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.common.util.Maps;
import ru.complitex.jedani.worker.entity.UserHistory;
import ru.complitex.jedani.worker.entity.Worker;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 9:22
 */
public class WorkerMapper extends BaseMapper {
    private static AtomicLong jidIndex = new AtomicLong(0);

    public String getNewJId(){
        String maxJId = sqlSession().selectOne("selectMaxJId");

        long jid = Long.parseLong(maxJId);

        if (jidIndex.get() < jid){
            jidIndex.set(jid);
        }

        return StringUtils.leftPad(jidIndex.incrementAndGet() + "", 6, '0');
    }

    public boolean isExistJId(Long objectId, String jId){
        return sqlSession().selectOne("selectIsExistJId", Maps.of("objectId", objectId, "jId", jId));
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

    public void insert(UserHistory userHistory){
        sqlSession().insert("insertUserHistory", userHistory);
    }
}

