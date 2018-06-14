package ru.complitex.jedani.worker.graph;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.template.PackageTextTemplate;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.graph.resource.CytoscapeCoseJsResourceReference;
import ru.complitex.jedani.worker.mapper.WorkerMapper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 13.06.2018 18:07
 */
public class WorkerGraphPanel extends Panel {
    @Inject
    private WorkerMapper workerMapper;

    private String elements;

    public WorkerGraphPanel(String id, Worker worker) {
        super(id);

        List<Worker> workers = new ArrayList<>(workerMapper.getWorkers(FilterWrapper.of(
                new Worker(worker.getLeft(), worker.getRight(), worker.getLevel())).setFilter("level3")));

        elements =  " {data: {id: '" + worker.getObjectId() + "', " +
                "label: '" + worker.getText(Worker.J_ID) + "\\n" +
                worker.getText(Worker.LAST_NAME) + "\\n" +
                worker.getText(Worker.FIRST_NAME) + "\\n" +
                worker.getText(Worker.MIDDLE_NAME) + "'}}";

        elements += "," + workers.stream()
                .map(w -> " {data: {id: '" + w.getObjectId() + "', " +
                        "label: '" + w.getText(Worker.J_ID) + "\\n" +
                        w.getText(Worker.LAST_NAME) + "\\n" +
                        w.getText(Worker.FIRST_NAME) + "\\n" +
                        w.getText(Worker.MIDDLE_NAME) + "'}}")
                .collect(Collectors.joining(","));

        elements += "," + workers.stream()
                .map(w -> " {data: {id: 'e" + w.getObjectId() + "', " +
                        "source: '" + w.getNumber(Worker.MANAGER_ID) + "', " +
                        "target: '" + w.getObjectId() + "'}}")
                .collect(Collectors.joining(","));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forReference(CytoscapeCoseJsResourceReference.INSTANCE));

        response.render(OnDomReadyHeaderItem.forScript(new PackageTextTemplate(WorkerGraphPanel.class,
                "resource/js/worker-graph.tmpl.js")
                .asString(new HashMap<String, String>(){{
                    put("elements", elements);
                }})));
    }
}
