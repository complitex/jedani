package ru.complitex.graph;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;

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

        List<Domain> workers = factory.openSession().selectList("selectDomains", FilterWrapper.of(new Domain("worker")));

        List<String> graph = workers.stream()
                .map(d -> {
                    Attribute a = d.getAttribute(17L);

                    if (a != null && a.getText() != null){
                        String[] s = a.getText().split("/");

                        if (s.length >= 2){
                            return s[s.length - 1] + "," +
                                    s[s.length - 2] + "," +
                                    d.getAttribute(1L).getText() + "," +
                                    d.getAttribute(10L).getText() + "," +
                                    d.getAttribute(11L).getText() + "," +
                                    d.getAttribute(12L).getText();
                        }
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
