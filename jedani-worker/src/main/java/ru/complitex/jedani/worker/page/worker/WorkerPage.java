package ru.complitex.jedani.worker.page.worker;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.wicket.form.HorizontalInputPanel;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.page.BasePage;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 5:57
 */
public class WorkerPage extends BasePage{
    @Inject
    private DomainMapper domainMapper;

    public WorkerPage(PageParameters parameters) {
        Long objectId = parameters.get("id").toOptionalLong();

        Domain worker = objectId != null ? domainMapper.getDomain("worker", objectId) : new Worker();

        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        BootstrapForm form = new BootstrapForm("form");
        add(form);

        form.add(new HorizontalInputPanel<>("lastName", new PropertyModel<>(worker.getAttribute(Worker.LAST_NAME), "text")));
        form.add(new HorizontalInputPanel<>("firstName", new PropertyModel<>(worker.getAttribute(Worker.FIRST_NAME), "text"))); //todo fio ref
        form.add(new HorizontalInputPanel<>("secondName", new PropertyModel<>(worker.getAttribute(Worker.SECOND_NAME), "text")));
        form.add(new HorizontalInputPanel<>("birthday", new PropertyModel<>(worker.getAttribute(Worker.BIRTHDAY), "text")));
        form.add(new HorizontalInputPanel<>("phone", new PropertyModel<>(worker.getAttribute(Worker.PHONE), "text")));
        form.add(new HorizontalInputPanel<>("email", new PropertyModel<>(worker.getAttribute(Worker.EMAIL), "text")));
        form.add(new HorizontalInputPanel<>("jId", new PropertyModel<>(worker.getAttribute(Worker.J_ID), "text")));
        form.add(new HorizontalInputPanel<>("mkStatus", new PropertyModel<>(worker.getAttribute(Worker.MK_STATUS), "text")));
        form.add(new HorizontalInputPanel<>("city", new PropertyModel<>(worker.getAttribute(Worker.CITY_ID), "number")));
        //todo position
        //todo regions
        //todo login password
        //todo user group
        //todo month payment

        //todo subworkers


    }
}
