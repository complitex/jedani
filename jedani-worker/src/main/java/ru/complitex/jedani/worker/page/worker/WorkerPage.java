package ru.complitex.jedani.worker.page.worker;

import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
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
import ru.complitex.common.wicket.util.ComponentUtil;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.component.datatable.DomainActionColumn;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.component.datatable.DomainIdColumn;
import ru.complitex.domain.component.form.AttributeInputListFormGroup;
import ru.complitex.domain.component.form.AttributeSelectFormGroup;
import ru.complitex.domain.component.form.AttributeSelectListFormGroup;
import ru.complitex.domain.component.form.DomainAutoCompleteFormGroup;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.mapper.DomainNodeMapper;
import ru.complitex.domain.mapper.EntityMapper;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.MkStatus;
import ru.complitex.jedani.worker.entity.Position;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.graph.WorkerGraphPanel;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.jedani.worker.service.WorkerService;
import ru.complitex.name.entity.FirstName;
import ru.complitex.name.entity.LastName;
import ru.complitex.name.entity.MiddleName;
import ru.complitex.name.service.NameService;
import ru.complitex.user.entity.User;
import ru.complitex.user.mapper.UserMapper;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static ru.complitex.jedani.worker.entity.Worker.*;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 5:57
 */

@AuthorizeInstantiation(JedaniRoles.AUTHORIZED)
public class WorkerPage extends BasePage {
    private Logger log = LoggerFactory.getLogger(Worker.class);

    @Inject
    private EntityMapper entityMapper;

    @Inject
    private DomainMapper domainMapper;

    @Inject
    private UserMapper userMapper;

    @Inject
    private WorkerMapper workerMapper;

    @Inject
    private NameService nameService;

    @Inject
    private WorkerService workerService;

    @Inject
    private EntityService entityService;

    @Inject
    private DomainNodeMapper domainNodeMapper;

    private Worker worker;
    private Worker manager;

    private IModel<Boolean> showGraph = Model.of(false);

    public WorkerPage(PageParameters parameters) {
        Long id = parameters.get("id").toOptionalLong();

        if (!parameters.get("new").isNull()){
            worker = new Worker();
            worker.init();

            worker.setText(J_ID, workerMapper.getNewJId());

            if (id != null) {
                manager = workerMapper.getWorker(id);

                manager.getNumberValues(REGION_IDS).forEach(n -> worker.addNumberValue(REGION_IDS, n));
                manager.getNumberValues(CITY_IDS).forEach(n -> worker.addNumberValue(CITY_IDS, n));
            }
        }else{
            if (id != null) {
                worker = workerMapper.getWorker(id);
            } else {
                worker = getCurrentWorker();
            }

            if (worker.getNumber(Worker.MANAGER_ID) != null && worker.getNumber(Worker.MANAGER_ID) != 1) {
                manager = workerMapper.getWorker(worker.getNumber(Worker.MANAGER_ID));
            }
        }

        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        //Data provider
        DataProvider<Worker> dataProvider = new DataProvider<Worker>(FilterWrapper.of(new Worker(worker.getLeft(), worker.getRight()))) {
            @Override
            public Iterator<Worker> iterator(long first, long count) {
                FilterWrapper<Worker> filterWrapper = getFilterState().limit(first, count);

                if (getSort() != null) {
                    filterWrapper.setSortProperty(getSort().getProperty());
                    filterWrapper.setAscending(getSort().isAscending());
                }

                return workerMapper.getWorkers(filterWrapper).iterator();
            }

            @Override
            public long size() {
                return workerMapper.getWorkersCount(getFilterState());
            }
        };

        //Worker
        FilterForm<FilterWrapper<Worker>> form = new FilterForm<>("form", dataProvider);
        add(form);

        DomainAutoCompleteFormGroup lastName, firstName, middleName;

        form.add(lastName = new DomainAutoCompleteFormGroup("lastName", "last_name", LastName.NAME,
                new PropertyModel<>(worker.getAttribute(Worker.LAST_NAME), "number")).setRequired(true));
        form.add(firstName = new DomainAutoCompleteFormGroup("firstName", "first_name", FirstName.NAME,
                new PropertyModel<>(worker.getAttribute(Worker.FIRST_NAME), "number")).setRequired(true));
        form.add(middleName = new DomainAutoCompleteFormGroup("middleName", "middle_name", MiddleName.NAME,
                new PropertyModel<>(worker.getAttribute(Worker.MIDDLE_NAME), "number")));
        form.add(new AttributeSelectFormGroup("position", new PropertyModel<>(worker.getAttribute(Worker.POSITION_ID), "number"),
                Position.ENTITY_NAME, Position.NAME));


        TextFieldFormGroup<String> jId = new TextFieldFormGroup<>("jId", new PropertyModel<>(worker.getAttribute(Worker.J_ID), "text"));
        jId.setRequired(true);

        if (worker.getObjectId() == null) {
            jId.onUpdate(target -> {
                if (workerMapper.isExistJId(jId.getTextField().getInput())) {
                    jId.getTextField().error(getString("error_jid_exist"));
                }

                target.add(jId);
            });
        }

        form.add(jId);

        form.add(new AttributeSelectFormGroup("mkStatus", new PropertyModel<>(worker.getAttribute(Worker.MK_STATUS_ID), "number"),
                MkStatus.ENTITY_NAME, MkStatus.NAME));
        form.add(new DateTextFieldFormGroup("birthday", new PropertyModel<>(worker.getAttribute(Worker.BIRTHDAY), "date")));
        form.add(new AttributeInputListFormGroup("phone", Model.of(worker.getOrCreateAttribute(Worker.PHONE))).setRequired(true));
        form.add(new TextFieldFormGroup<>("email", new PropertyModel<>(worker.getAttribute(Worker.EMAIL), "text")));

        AttributeSelectListFormGroup city, region;
        form.add(region = new AttributeSelectListFormGroup("region", Model.of(worker.getOrCreateAttribute(Worker.REGION_IDS)),
                Region.ENTITY_NAME, Region.NAME).setRequired(true));
        form.add(city = new AttributeSelectListFormGroup("city", Model.of(worker.getOrCreateAttribute(Worker.CITY_IDS)),
                City.ENTITY_NAME, City.NAME, region.getListModel()).setRequired(true));
        region.onChange(t -> t.add(city));

        //User
        User user = worker.getParentId() != null ? userMapper.getUser(worker.getParentId()) : new User(worker.getText(J_ID));

        TextField<String> login = new TextField<>("login", new PropertyModel<>(user, "login"));
        login.setRequired(true);
        login.setEnabled(user.getId() == null);
        form.add(login);

        PasswordTextField password = new PasswordTextField("password", new PropertyModel<>(user, "password"));
        password.setRequired(false);
        form.add(password);

        PasswordTextField confirmPassword = new PasswordTextField("confirmPassword", new PropertyModel<>(user, "confirmPassword"));
        confirmPassword.setRequired(false);
        form.add(confirmPassword);

        form.add(new DateTextFieldFormGroup("registrationDate", new PropertyModel<>(worker.getAttribute(Worker.INVOLVED_AT), "date"))
                .onUpdate(target -> target.add(get("form:registrationDate")))
                .setRequired(true));

        //Manager
        String managerFio = "";
        String managerPhones = "";
        String managerEmail = "";

        if (manager != null) {
            managerFio = workerService.getWorkerFio(manager, getLocale());
            managerPhones = String.join(", ", manager.getTextValues(Worker.PHONE));
            managerEmail = manager.getText(Worker.EMAIL);
        }

        form.add(new Label("managerFio", managerFio));
        form.add(new Label("managerPhones", managerPhones));
        form.add(new Label("managerEmail", managerEmail));


        form.add(new IndicatingAjaxButton("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    //User
                    if (!Strings.isNullOrEmpty(user.getPassword())){
                        if (!user.getPassword().equals(user.getConfirmPassword())){
                            password.error(getString("error_confirm_password"));
                            target.add(feedback);
                            return;
                        }

                        user.setPassword(Hashing.sha256().hashString(user.getPassword(), StandardCharsets.UTF_8).toString());
                    }

                    if (user.getId() == null){
                        if (userMapper.getUser(user.getLogin()) != null){
                            login.error(getString("error_login_exist"));
                            target.add(feedback);
                            return;
                        }

                        if (Strings.isNullOrEmpty(user.getPassword())){
                            password.error(getString("error_empty_password"));
                            target.add(feedback);
                            return;
                        }

                        userMapper.insertUser(user);
                    }else if (!Strings.isNullOrEmpty(user.getPassword())){
                        userMapper.updateUserPassword(user);
                    }

                    //Worker
                    worker.setParentId(user.getId());
                    worker.setParentEntityId(User.ENTITY_ID);

                    worker.setNumber(LAST_NAME, nameService.getOrCreateLastName(lastName.getInput(), worker.getNumber(LAST_NAME)));
                    worker.setNumber(FIRST_NAME, nameService.getOrCreateFirstName(firstName.getInput(), worker.getNumber(FIRST_NAME)));
                    worker.setNumber(MIDDLE_NAME, nameService.getOrCreateMiddleName(middleName.getInput(), worker.getNumber(MIDDLE_NAME)));

                    //todo uniquie jid

                    if (manager != null) {
                        worker.setNumber(Worker.MANAGER_ID, manager.getObjectId());
                    }else{
                        worker.setNumber(Worker.MANAGER_ID, 1L);
                        manager = workerMapper.getWorker(1L);
                    }

                    if (worker.getObjectId() == null){
                        domainMapper.insertDomain(worker);

                        domainNodeMapper.updateIndex(manager, worker);

                        getSession().info(getString("info_user_created"));
                        target.add(feedback);
                    }else{
                        domainMapper.updateDomain(worker);

                        getSession().info(getString("info_user_updated"));
                        target.add(feedback);
                    }

                    if (isAdmin()){
                        if (manager != null && manager.getObjectId() != 1L) {
                            setResponsePage(WorkerPage.class, new PageParameters().add("id", manager.getObjectId()));
                        } else {
                            setResponsePage(WorkerListPage.class);
                        }
                    }else{
                        setResponsePage(WorkerPage.class);
                    }
                } catch (Exception e) {
                    error(e.getMessage());
                    target.add(feedback);

                    log.error("error save worker ", e);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(feedback);

                form.visitFormComponents((object, visit) -> {
                    if (object.hasErrorMessage()){
                        target.add(ComponentUtil.getAjaxParent(object));
                    }
                });

                target.add(form);
            }
        });

        form.add(new AjaxButton("create") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                setResponsePage(WorkerPage.class, new PageParameters().add("id", worker.getObjectId())
                        .add("new", ""));
            }

            @Override
            public boolean isVisible() {
                return worker.getObjectId() != null;
            }
        }.setDefaultFormProcessing(false));

        AjaxButton cancel = new AjaxButton("cancel") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                if (isAdmin()){
                    if (manager != null){
                        setResponsePage(WorkerPage.class, new PageParameters().add("id", manager.getObjectId()));
                    }else{
                        setResponsePage(WorkerListPage.class);
                    }
                }else{
                    setResponsePage(WorkerPage.class);
                }
            }
        };

        cancel.setDefaultFormProcessing(false);
        cancel.add(new Label("label", getString(worker.getObjectId() == null ? "cancel" : "back")));

        form.add(cancel);

        //Structure
        WebMarkupContainer structure = new WebMarkupContainer("structure");
        structure.setOutputMarkupId(true);
        structure.setVisible(worker.getObjectId() != null);
        form.add(structure);

        List<IColumn<Worker, SortProperty>> columns = new ArrayList<>();

        columns.add(new DomainIdColumn<>());
        getEntityAttributes().forEach(a -> columns.add(new DomainColumn<>(a)));

        columns.add(new AbstractDomainColumn<Worker>(new ResourceModel("subWorkersCount"),
                new SortProperty("subWorkersCount")) {
            @Override
            public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getSubWorkerCount()));
            }

            @Override
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new TextFilter<>(componentId, Model.of(""), form);
            }
        });

        columns.add(new AbstractDomainColumn<Worker>(new ResourceModel("level"), new SortProperty("level")) {
            @Override
            public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getLevel()));
            }

            @Override
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new TextFilter<>(componentId, Model.of(""), form);
            }
        });

        columns.add(new DomainActionColumn<>(WorkerPage.class));

        FilterDataTable<Worker> table = new FilterDataTable<Worker>("table", columns, dataProvider, form, 10){
            @Override
            public boolean isVisible() {
                return !showGraph.getObject();
            }
        };
        table.setHideOnEmpty(worker.getObjectId() == null);
        structure.add(table);

        if (worker.getObjectId() != null) {
            structure.add(new WorkerGraphPanel("graph", worker){
                @Override
                public boolean isVisible() {
                    return showGraph.getObject();
                }
            });
        }else{
            structure.add(new EmptyPanel("graph"));
        }

        structure.add(new AjaxLink<Void>("tableLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                showGraph.setObject(false);
                target.add(structure);
            }
        }.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                if (!showGraph.getObject()){
                    oldClasses.add("active");
                }

                return oldClasses;
            }
        }));

        structure.add(new AjaxLink<Void>("graphLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                showGraph.setObject(true);
                target.add(structure);
            }
        }.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                if (showGraph.getObject()){
                    oldClasses.add("active");
                }

                return oldClasses;
            }
        }));

        structure.add(new Link<Void>("printLink") {
            @Override
            public void onClick() {
                setResponsePage(WorkerPrintPage.class, new PageParameters().add("id", worker.getObjectId()));
            }
        });
    }

    private List<EntityAttribute> getEntityAttributes() {
        Entity entity = entityMapper.getEntity(Worker.ENTITY_NAME);

        List<EntityAttribute> list = new ArrayList<>();

        entityService.setRefEntityAttribute(entity, LAST_NAME, LastName.ENTITY_NAME, LastName.NAME);
        list.add(entity.getEntityAttribute(LAST_NAME));

        entityService.setRefEntityAttribute(entity, FIRST_NAME, FirstName.ENTITY_NAME, FirstName.NAME);
        list.add(entity.getEntityAttribute(FIRST_NAME));

        entityService.setRefEntityAttribute(entity, MIDDLE_NAME, MiddleName.ENTITY_NAME, MiddleName.NAME);
        list.add(entity.getEntityAttribute(MIDDLE_NAME));

        list.add(entity.getEntityAttribute(J_ID));

        entityService.setRefEntityAttribute(entity, REGION_IDS, Region.ENTITY_NAME, Region.NAME);
        list.add(entity.getEntityAttribute(REGION_IDS));

        entityService.setRefEntityAttribute(entity, CITY_IDS, City.ENTITY_NAME, City.NAME);
        list.add(entity.getEntityAttribute(CITY_IDS));

        return list;
    }
}
