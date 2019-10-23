package ru.complitex.jedani.worker.mapper;

import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.jedani.worker.entity.WorkerNode;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 16.10.2019 11:01 AM
 */
public class WorkerNodeMapper extends BaseMapper {
    public List<WorkerNode> getAllWorkerNodes(){
        return sqlSession().selectList("selectAllWorkerNodes");
    }

}
