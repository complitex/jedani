package ru.complitex.jedani.worker.page.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.Region;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.datatable.DataProvider;
import ru.complitex.common.wicket.datatable.FilterDataTable;
import ru.complitex.common.wicket.form.DateTextFieldFormGroup;
import ru.complitex.common.wicket.form.TextFieldFormGroup;
import ru.complitex.domain.component.datatable.DomainActionColumn;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.component.datatable.DomainIdColumn;
import ru.complitex.domain.component.form.AttributeListFormGroup;
import ru.complitex.domain.component.form.AttributeSelectFormGroup;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.mapper.EntityMapper;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.user.entity.User;
import ru.complitex.user.mapper.UserMapper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.complitex.jedani.worker.entity.Worker.*;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 5:57
 */
public class WorkerPage extends BasePage{
    private Logger log = LoggerFactory.getLogger(Worker.class);

    @Inject
    private transient EntityMapper entityMapper;

    @Inject
    private transient DomainMapper domainMapper;

    @Inject
    private transient UserMapper userMapper;

    public WorkerPage(PageParameters parameters) {
        Long objectId = parameters.get("id").toOptionalLong();

        //todo dev worker page

        Domain worker = objectId != null ? domainMapper.getDomain("worker", objectId) : new Worker();

        if (worker == null){
            throw new WicketRuntimeException("worker not found");
        }

        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        String[] levels = worker.getText(Worker.FULL_ANCESTRY_PATH).split("/");

        //Data provider

        DataProvider<Domain> dataProvider = new DataProvider<Domain>(FilterWrapper.of(new Domain("worker")) //todo generic
                .add("entityAttributeId", Worker.ANCESTRY)
                .add("endWith", "/" + levels[levels.length - 1])) {
            @Override
            public Iterator<? extends Domain> iterator(long first, long count) {
                FilterWrapper<Domain> filterWrapper = getFilterState().limit(first, count);

                if (getSort() != null){
                    filterWrapper.setSortProperty(getSort().getProperty());
                    filterWrapper.setAscending(getSort().isAscending());
                }

                return domainMapper.getDomains(filterWrapper).iterator();
            }

            @Override
            public long size() {
                return domainMapper.getDomainsCount(getFilterState());
            }
        };

        //Worker

        FilterForm<FilterWrapper<Domain>> form = new FilterForm<>("form", dataProvider);
        add(form);

        form.add(new TextFieldFormGroup<>("lastName", new PropertyModel<>(worker.getAttribute(Worker.LAST_NAME), "text")));
        form.add(new TextFieldFormGroup<>("firstName", new PropertyModel<>(worker.getAttribute(Worker.FIRST_NAME), "text"))); //todo fio ref
        form.add(new TextFieldFormGroup<>("secondName", new PropertyModel<>(worker.getAttribute(Worker.SECOND_NAME), "text")));
        form.add(new TextFieldFormGroup<>("managerRankId", new PropertyModel<>(worker.getAttribute(Worker.MANAGER_RANK_ID), "text")));
        form.add(new TextFieldFormGroup<>("jId", new PropertyModel<>(worker.getAttribute(Worker.J_ID), "text")));
        form.add(new TextFieldFormGroup<>("mkStatus", new PropertyModel<>(worker.getAttribute(Worker.MK_STATUS), "text")));
        form.add(new DateTextFieldFormGroup("birthday", new PropertyModel<>(worker.getAttribute(Worker.BIRTHDAY), "date")));
        form.add(new AttributeListFormGroup("phone", Model.of(worker.getOrCreateAttribute(Worker.PHONE))));
        form.add(new TextFieldFormGroup<>("email", new PropertyModel<>(worker.getAttribute(Worker.EMAIL), "text")));

        AttributeSelectFormGroup city, region;
        form.add(region = new AttributeSelectFormGroup("region", Model.of(worker.getAttribute(Worker.REGION_ID)), "region", Region.NAME));
        form.add(city = new AttributeSelectFormGroup("city", Model.of(worker.getAttribute(Worker.CITY_ID)), "city", City.NAME, region.getListModel()));
        region.setOnChange(t -> t.add(city));

        //User

        User user = userMapper.getUser(worker.getParentId());

        form.add(new Label("user", Model.of(user.getLogin())));

        PasswordTextField password = new PasswordTextField("password");
        form.add(password);

        PasswordTextField confirmPassword = new PasswordTextField("confirmPassword");
        form.add(confirmPassword);

        form.add(new Label("registrationDate", Model.of(worker.getAttribute(INVOLVED_AT).getDate())));

        //todo position
        //todo regions
        //todo login password
        //todo user group
        //todo month payment

        //todo subworkers

        form.add(new AjaxButton("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    log.info(new ObjectMapper().writer().writeValueAsString(worker));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });

        form.add(new AjaxButton("cancel") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                setResponsePage(WorkerPage.class, parameters);
            }
        }.setDefaultFormProcessing(false));

        //Structure

        List<IColumn<Domain, SortProperty>> columns = new ArrayList<>();

        columns.add(new DomainIdColumn());
        getEntityAttributes().forEach(a -> columns.add(new DomainColumn(a) {
            @Override
            protected EntityMapper getEntityMapper() {
                return entityMapper;
            }

            @Override
            protected DomainMapper getDomainMapper() {
                return domainMapper;
            }
        }));
        columns.add(new DomainActionColumn(WorkerPage.class));

        FilterDataTable<Domain> table = new FilterDataTable<>("table", columns, dataProvider, form, 10);
        form.add(table);
    }

    private List<EntityAttribute> getEntityAttributes() {
        Entity entity = entityMapper.getEntity("worker");

        return Stream.of(J_ID, CREATED_AT, FIRST_NAME, SECOND_NAME, LAST_NAME, BIRTHDAY, PHONE, EMAIL, CITY_ID) //todo regions
                .map(entity::getEntityAttribute).collect(Collectors.toList());
    }
}
