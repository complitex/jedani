package ru.complitex.jedani.worker.page.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
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
import ru.complitex.domain.component.form.DomainAutoCompleteFormGroup;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.mapper.EntityMapper;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.name.entity.FirstName;
import ru.complitex.name.entity.LastName;
import ru.complitex.name.entity.MiddleName;
import ru.complitex.user.entity.User;
import ru.complitex.user.mapper.UserMapper;

import javax.inject.Inject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.complitex.jedani.worker.entity.Worker.*;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 5:57
 */

@AuthorizeInstantiation(JedaniRoles.AUTHORIZED)
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

        boolean admin = isAdmin();

        User currentUser = userMapper.getUser(getLogin());
        Domain currentWorker = domainMapper.getDomainByParentId("worker", currentUser.getId());

        if (!admin){
            if (currentWorker != null){

            }else{
                throw new WicketRuntimeException("current worker is null");
            }
        }



        Domain worker = objectId != null ? domainMapper.getDomain("worker", objectId) : new Worker();

        if (worker == null){
            throw new WicketRuntimeException("worker not found");
        }

        //todo is admin


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

        form.add(new DomainAutoCompleteFormGroup("lastName", "last_name", LastName.NAME, new PropertyModel<>(worker.getAttribute(Worker.LAST_NAME), "number")));
        form.add(new DomainAutoCompleteFormGroup("firstName", "first_name", FirstName.NAME, new PropertyModel<>(worker.getAttribute(Worker.FIRST_NAME), "number")));
        form.add(new DomainAutoCompleteFormGroup("middleName", "middle_name", MiddleName.NAME, new PropertyModel<>(worker.getAttribute(Worker.MIDDLE_NAME), "number")));
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

        PasswordTextField password = new PasswordTextField("password", Model.of(""));
        password.setRequired(false);
        form.add(password);

        PasswordTextField confirmPassword = new PasswordTextField("confirmPassword", Model.of(""));
        confirmPassword.setRequired(false);
        form.add(confirmPassword);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date registrationDate = worker.getDate(INVOLVED_AT);
        form.add(new Label("registrationDate", registrationDate != null ? dateFormat.format(registrationDate) : ""));

        //Manager

        String managerFio = "";
        String managerPhones = "";
        String managerEmail = "";

        String ancestry = worker.getText(Worker.ANCESTRY);

        if (!Strings.isNullOrEmpty(ancestry)) {
            Domain manager = domainMapper.getDomainByExternalId("worker",
                    ancestry.substring(Math.max(ancestry.lastIndexOf('/') + 1, 0)));

            managerFio = domainMapper.getDomain("last_name", manager.getAttribute(Worker.LAST_NAME).getNumber()).getValueText(LastName.NAME, getLocale()) + " " +
                    domainMapper.getDomain("first_name", manager.getAttribute(Worker.FIRST_NAME).getNumber()).getValueText(FirstName.NAME, getLocale()) + " " +
                    domainMapper.getDomain("middle_name", manager.getAttribute(Worker.MIDDLE_NAME).getNumber()).getValueText(MiddleName.NAME, getLocale());

            try {
                List<String> list = new ObjectMapper().readValue(manager.getJson(Worker.PHONE), new TypeReference<List<String>>(){});
                managerPhones = String.join(", ", list);
            } catch (IOException e) {
                log.error("error parse phones ", e);
            }

            managerEmail = manager.getText(Worker.EMAIL);
        }

        form.add(new Label("managerFio", managerFio));
        form.add(new Label("managerPhones", managerPhones));
        form.add(new Label("managerEmail", managerEmail));


        form.add(new AjaxButton("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    log.info(new ObjectMapper().writer().writeValueAsString(worker));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(feedback);
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

        return Stream.of(J_ID, CREATED_AT, FIRST_NAME, MIDDLE_NAME, LAST_NAME, BIRTHDAY, PHONE, EMAIL, CITY_ID) //todo regions
                .map(entity::getEntityAttribute).collect(Collectors.toList());
    }
}
