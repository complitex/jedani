package ru.complitex.jedani.worker.page.worker;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.jedani.worker.graph.WorkerGraphPanel;
import ru.complitex.jedani.worker.graph.resource.FileSaverResourceReference;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
import ru.complitex.jedani.worker.security.JedaniRoles;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 14.06.2018 18:22
 */
@AuthorizeInstantiation(JedaniRoles.AUTHORIZED)
public class WorkerPrintPage extends WebPage {
    @Inject
    private WorkerMapper workerMapper;

    public WorkerPrintPage(PageParameters pageParameters) {
        add(new WorkerGraphPanel("graph", workerMapper.getWorker(pageParameters.get("id").toLongObject())));

        add(new Link<Void>("back") {
            @Override
            public void onClick() {
                setResponsePage(WorkerPage.class, pageParameters);
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forReference(FileSaverResourceReference.INSTANCE));
    }
}
