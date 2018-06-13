package ru.complitex.jedani.worker.graph;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Domain;
import ru.complitex.jedani.worker.entity.Worker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 01.03.2018 21:56
 */
public class WorkerGraphExport {
    public static void main(String[] args) throws IOException {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(WorkerGraphExport.class.getResourceAsStream("mybatis-graph-config.xml"));

        List<Domain> workers = factory.openSession().selectList("selectDomains", FilterWrapper.of(new Domain(Worker.ENTITY_NAME)));

        List<String> graph = workers.stream()
                .map(d -> {
                    if (d.getNumber(Worker.MANAGER_ID) != null){
                        return d.getNumber(Worker.MANAGER_ID) + "," +
                                d.getObjectId() + "," +
                                d.getText(Worker.J_ID) + "," +
                                d.getText(Worker.LAST_NAME) + "," +
                                d.getText(Worker.FIRST_NAME) + "," +
                                d.getText(Worker.LAST_NAME);
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        graph.add(0, "from, to, jid, first, second, last");

        Path path = Paths.get("jedani-worker-graph.csv");

        Files.write(path, graph);

        System.out.println("wrote worker graph " + path.toAbsolutePath() + " " + graph.size());
    }
}
