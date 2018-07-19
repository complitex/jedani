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
import ru.complitex.name.service.NameService;

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

    @Inject
    private NameService nameService;

    private String elements;
    private String fileName;

    public WorkerGraphPanel(String id, Worker worker, Long levelDepth) {
        super(id);

        List<Worker> workers = new ArrayList<>(workerMapper.getWorkers(FilterWrapper.of(
                new Worker(worker.getLeft(), worker.getRight(), worker.getLevel())).add("levelDepth", levelDepth)));

        elements =  " {data: {id: '" + worker.getObjectId() + "', " +
                "label: '" + worker.getText(Worker.J_ID) + "\\n" +
                nameService.getLastName(worker.getNumber(Worker.LAST_NAME)) + "\\n" +
                nameService.getFirstName(worker.getNumber(Worker.FIRST_NAME)) + "\\n" +
                nameService.getMiddleName(worker.getNumber(Worker.MIDDLE_NAME)) + "'}}";

        if (!workers.isEmpty()) {
            elements += "," + workers.stream()
                    .map(w -> {
                        int color = Math.max((int) (255 - 32*(w.getLevel() - worker.getLevel())), 128);

                        return " {data: {id: '" + w.getObjectId() + "', " +
                                "label: '" + w.getText(Worker.J_ID) + "\\n" +
                                nameService.getLastName(w.getNumber(Worker.LAST_NAME)) + "\\n" +
                                nameService.getFirstName(w.getNumber(Worker.FIRST_NAME)) + "\\n" +
                                nameService.getMiddleName(w.getNumber(Worker.MIDDLE_NAME)) + "'}, " +
                                "style: {'background-color': 'rgb("+color+", "+color+", "+color+")'}}";
                    })
                    .collect(Collectors.joining(","));

            elements += "," + workers.stream()
                    .map(w -> " {data: {id: 'e" + w.getObjectId() + "', " +
                            "source: '" + w.getNumber(Worker.MANAGER_ID) + "', " +
                            "target: '" + w.getObjectId() + "'}}")
                    .collect(Collectors.joining(","));
        }

        fileName =  worker.getText(Worker.J_ID) + " " +
                nameService.getLastName(worker.getNumber(Worker.LAST_NAME)) + " " +
                nameService.getFirstName(worker.getNumber(Worker.FIRST_NAME)) + " " +
                nameService.getMiddleName(worker.getNumber(Worker.MIDDLE_NAME));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forReference(CytoscapeCoseJsResourceReference.INSTANCE));

        response.render(OnDomReadyHeaderItem.forScript(new PackageTextTemplate(WorkerGraphPanel.class,
                "resource/js/worker-graph.tmpl.js")
                .asString(new HashMap<String, String>(){{
                    put("elements", elements);
                    put("fileName", fileName);
                }})));
    }
}
