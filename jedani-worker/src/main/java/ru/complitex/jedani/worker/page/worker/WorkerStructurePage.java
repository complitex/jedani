package ru.complitex.jedani.worker.page.worker;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.jedani.worker.graph.WorkerGraphPanel;
import ru.complitex.jedani.worker.graph.resource.FileSaverResourceReference;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.security.JedaniRoles;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 14.06.2018 18:22
 */
@AuthorizeInstantiation(JedaniRoles.AUTHORIZED)
public class WorkerStructurePage extends BasePage {
    @Inject
    private WorkerMapper workerMapper;

    public WorkerStructurePage(PageParameters pageParameters) {
        add(new WorkerGraphPanel("graph", workerMapper.getWorker(pageParameters.get("id").toLongObject()),
                pageParameters.get("level").toLongObject(), pageParameters.get("volume").toBoolean(false)));
        add(new AjaxLink<>("back") {
            @Override
            public void onClick(AjaxRequestTarget target) {
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
