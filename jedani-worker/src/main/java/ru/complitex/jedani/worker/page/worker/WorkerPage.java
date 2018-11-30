package ru.complitex.jedani.worker.page.worker;

import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.CityType;
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
import ru.complitex.domain.entity.*;
import ru.complitex.domain.mapper.AttributeMapper;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.mapper.DomainNodeMapper;
import ru.complitex.domain.mapper.EntityMapper;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.model.TextAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.component.WorkerAutoComplete;
import ru.complitex.jedani.worker.entity.MkStatus;
import ru.complitex.jedani.worker.entity.Position;
import ru.complitex.jedani.worker.entity.Worker;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static ru.complitex.jedani.worker.security.JedaniRoles.*;

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
    private DomainService domainService;

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

    @Inject
    private AttributeMapper attributeMapper;

    private Worker worker;
    private Worker manager;

    private IModel<Boolean> showGraph = Model.of(false);

    private boolean participant = true;

    public WorkerPage(PageParameters parameters) {
        Long id = parameters.get("id").toOptionalLong();

        boolean backToWorkerList = !parameters.get("a").isNull();

        if (!parameters.get("new").isNull()){
            worker = new Worker();
            worker.init();

            participant = !Objects.equals(parameters.get("new").toOptionalString(), "employee");

            if (participant) {
                worker.setText(Worker.J_ID, workerMapper.getNewJId());

                if (id != null) {
                    manager = workerMapper.getWorker(id);
                }else if (getCurrentWorker().getObjectId() != null){
                    manager = workerMapper.getWorker(getCurrentWorker().getObjectId());
                }

                if (manager != null){
                    manager.getNumberValues(Worker.REGIONS).forEach(n -> worker.addNumberValue(Worker.REGIONS, n));
                    manager.getNumberValues(Worker.CITIES).forEach(n -> worker.addNumberValue(Worker.CITIES, n));

                    worker.setNumber(Worker.MANAGER_ID, manager.getObjectId());
                }
            }else{
                worker.setNumber(Worker.EMPLOYEE, 1L);
            }
        }else{
            if (id != null) {
                worker = workerMapper.getWorker(id);
            } else {
                worker = getCurrentWorker();
            }

            participant = !Objects.equals(worker.getNumber(Worker.EMPLOYEE), 1L);

            if (worker.getNumber(Worker.MANAGER_ID) != null && worker.getNumber(Worker.MANAGER_ID) != 1) {
                manager = workerMapper.getWorker(worker.getNumber(Worker.MANAGER_ID));
            }
        }

        if (worker.getObjectId() != null && participant){
            if (!isAdmin() && !isStructureAdmin()){
                if (getCurrentWorker().getRight() < worker.getRight() || getCurrentWorker().getLeft() > worker.getLeft()){
                    throw new UnauthorizedInstantiationException(getClass());
                }
            }
        }

        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        //Data provider
        FilterWrapper<Worker> filterWrapper = FilterWrapper.of(new Worker(worker.getLeft(), worker.getRight(), worker.getLevel()));

        DataProvider<Worker> dataProvider = new DataProvider<Worker>(filterWrapper) {
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

        form.add(lastName = new DomainAutoCompleteFormGroup("lastName", LastName.ENTITY_NAME, LastName.NAME,
                new NumberAttributeModel(worker, Worker.LAST_NAME), true).setRequired(true));
        form.add(firstName = new DomainAutoCompleteFormGroup("firstName", FirstName.ENTITY_NAME, FirstName.NAME,
                new NumberAttributeModel(worker, Worker.FIRST_NAME), true).setRequired(true));
        form.add(middleName = new DomainAutoCompleteFormGroup("middleName", MiddleName.ENTITY_NAME, MiddleName.NAME,
                new NumberAttributeModel(worker, Worker.MIDDLE_NAME), true));
        form.add(new AttributeSelectFormGroup("position", new NumberAttributeModel(worker, Worker.POSITION),
                Position.ENTITY_NAME, Position.NAME));

        TextFieldFormGroup<String> jId = new TextFieldFormGroup<>("jId", new PropertyModel<>(worker.getOrCreateAttribute(Worker.J_ID), "text"));
        jId.getTextField().setRequired(participant);

        if (worker.getObjectId() == null) {
            jId.onUpdate(target -> {
                if (workerMapper.isExistJId(null, jId.getTextField().getInput())) {
                    jId.getTextField().error(getString("error_jid_exist"));
                }

                target.add(jId);
            });
        }
        jId.setVisible(participant);
        form.add(jId);

        //num
        form.add(new TextFieldFormGroup<>("num", Model.of("")).setVisible(!participant));

        form.add(new AttributeSelectFormGroup("mkStatus", new NumberAttributeModel(worker, Worker.MK_STATUS),
                MkStatus.ENTITY_NAME, MkStatus.NAME).setVisible(participant));
        form.add(new DateTextFieldFormGroup("birthday", new PropertyModel<>(worker.getOrCreateAttribute(Worker.BIRTHDAY), "date")));
        form.add(new AttributeInputListFormGroup("phone", Model.of(worker.getOrCreateAttribute(Worker.PHONE))).setRequired(participant));
        form.add(new TextFieldFormGroup<>("email", new TextAttributeModel(worker, Worker.EMAIL, TextAttributeModel.TYPE.LOWER_CASE)));

        AttributeSelectListFormGroup city, region;
        form.add(region = new AttributeSelectListFormGroup("region", Model.of(worker.getOrCreateAttribute(Worker.REGIONS)),
                Region.ENTITY_NAME, Region.NAME, true).setRequired(participant));
        form.add(city = new AttributeSelectListFormGroup("city", Model.of(worker.getOrCreateAttribute(Worker.CITIES)),
                City.ENTITY_NAME, City.NAME, region.getListModel(), true){
            @Override
            protected String getPrefix(Domain domain) {
                Long cityTypeId = domain.getNumber(City.CITY_TYPE);

                if (cityTypeId != null){
                    Domain cityType = domainMapper.getDomain(CityType.ENTITY_NAME, cityTypeId);

                    if (cityType != null){
                        return cityType.getValueText(CityType.SHORT_NAME).toLowerCase() + " ";
                    }
                }

                return super.getPrefix(domain);
            }
        }.setRequired(participant));
        region.onChange(t -> t.add(city));

        //User
        User user = worker.getParentId() != null
                ? userMapper.getUser(worker.getParentId())
                : new User(participant ? worker.getText(Worker.J_ID) : null);

        TextField<String> login = new TextField<>("login", new PropertyModel<>(user, "login"));
        login.setRequired(true);
        form.add(login);

        PasswordTextField password = new PasswordTextField("password", new PropertyModel<>(user, "password"));
        password.setRequired(false);
        form.add(password);

        PasswordTextField confirmPassword = new PasswordTextField("confirmPassword", new PropertyModel<>(user, "confirmPassword"));
        confirmPassword.setRequired(false);
        form.add(confirmPassword);

        form.add(new DateTextFieldFormGroup("registrationDate", new PropertyModel<>(worker.getOrCreateAttribute(Worker.INVOLVED_AT), "date"))
                .onUpdate(target -> target.add(get("form:registrationDate")))
                .setRequired(participant));

        //User group
        WebMarkupContainer userGroups = new WebMarkupContainer("userGroups");
        userGroups.setVisible(!Objects.equals(getCurrentUser().getId(), user.getId()));
        form.add(userGroups);

        WebMarkupContainer adminRoleLabel = new WebMarkupContainer("adminRoleLabel");
        adminRoleLabel.setVisible(isAdmin());
        userGroups.add(adminRoleLabel);

        CheckBox adminRole = new CheckBox("adminRole", Model.of(user.hasRole(ADMINISTRATORS)));
        adminRoleLabel.add(adminRole);

        WebMarkupContainer structureAdminRoleLabel = new WebMarkupContainer("structureAdminRoleLabel");
        structureAdminRoleLabel.setVisible(isAdmin() || isStructureAdmin());
        userGroups.add(structureAdminRoleLabel);

        CheckBox structureAdminRole = new CheckBox("structureAdminRole", Model.of(user.hasRole(STRUCTURE_ADMINISTRATORS)));
        structureAdminRoleLabel.add(structureAdminRole);

        CheckBox userRole = new CheckBox("userRole", Model.of(user.hasRole(USERS)));
        userGroups.add(userRole);

        WebMarkupContainer managerContainer = new WebMarkupContainer("manager");
        managerContainer.setVisible(participant);
        form.add(managerContainer);

        //Manager
        Label managerPhone = new Label("managerPhones", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return manager != null ? String.join(", ", manager.getTextValues(Worker.PHONE)) : null;
            }
        });
        managerPhone.setOutputMarkupId(true);
        managerContainer.add(managerPhone);

        Label managerEmail = new Label("managerEmail", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return manager != null ? StringUtils.lowerCase(manager.getText(Worker.EMAIL)): null;
            }
        });
        managerEmail.setOutputMarkupId(true);
        managerContainer.add(managerEmail);

        managerContainer.add(new WorkerAutoComplete("managerFio", new PropertyModel<>(worker.getOrCreateAttribute(Worker.MANAGER_ID), "number")){
            @Override
            protected void onChange(AjaxRequestTarget target) {
                manager = workerMapper.getWorker(worker.getNumber(Worker.MANAGER_ID));

                target.add(managerPhone, managerEmail);
            }
        });

        //Structure
        WebMarkupContainer structure = new WebMarkupContainer("structure");
        structure.setOutputMarkupId(true);
        structure.setVisible(worker.getObjectId() != null && participant);
        form.add(structure);

        //History
        WebMarkupContainer historyHeader = new WebMarkupContainer("historyHeader");
        historyHeader.setVisible(worker.getObjectId() != null);
        form.add(historyHeader);

        WebMarkupContainer history = new WebMarkupContainer("history");
        history.setOutputMarkupId(true);
        form.add(history);

        Entity workerEntity = entityMapper.getEntity(Worker.ENTITY_NAME);

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");


        history.add(new ListView<Attribute>("attributes", new LoadableDetachableModel<List<Attribute>>() {
            @Override
            protected List<Attribute> load() {
                return attributeMapper.getHistoryAttributes(Worker.ENTITY_NAME, worker.getObjectId());
            }
        }) {
            @Override
            protected void populateItem(ListItem<Attribute> item) {
                Attribute attribute = item.getModelObject();

                EntityAttribute entityAttribute = workerEntity.getEntityAttribute(attribute.getEntityAttributeId());

                String name = entityAttribute.getValue() != null
                        ? entityAttribute.getValue().getText()
                        : entityAttribute.getEntityAttributeId().toString();

                item.add(new Label("date", dateTimeFormat.format(attribute.getStartDate())));
                item.add(new Label("name", name));

                String value = "";

                switch (entityAttribute.getValueType()){
                    case TEXT_VALUE:
                        value = attribute.getValues().stream()
                                .map(Value::getText)
                                .collect(Collectors.joining(","));

                        break;
                    case NUMBER_VALUE:
                        value = attribute.getValues().stream()
                                .map(v -> String.valueOf(v.getNumber()))
                                .collect(Collectors.joining(","));

                        break;
                    case TEXT:
                    case BOOLEAN:
                    case DECIMAL:
                        value = attribute.getText();

                        break;
                    case NUMBER:
                        value = String.valueOf(attribute.getNumber());

                        break;
                    case DATE:
                        value = String.valueOf(attribute.getDate());

                        break;
                    case ENTITY_VALUE:
                        value = attribute.getValues().stream()
                                .map(v -> String.valueOf(v.getNumber()))
                                .collect(Collectors.joining(","));

                        break;
                    case ENTITY:
                        value = String.valueOf(attribute.getNumber());

                        break;
                }

                item.add(new Label("value", value));
            }
        }.setVisible(worker.getObjectId() != null));

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


                    if (adminRole.getModelObject()){
                        user.addRole(ADMINISTRATORS);
                    }else{
                        user.removeRole(ADMINISTRATORS);
                    }

                    if (structureAdminRole.getModelObject()){
                        user.addRole(STRUCTURE_ADMINISTRATORS);
                    }else{
                        user.removeRole(STRUCTURE_ADMINISTRATORS);
                    }

                    if (userRole.getModelObject()){
                        user.addRole(USERS);
                    }else{
                        user.removeRole(USERS);
                    }

                    if (user.getId() == null){
                        if (userMapper.getUser(user.getLogin()) != null){
                            login.error(getString("error_login_exist"));
                            target.add(feedback);
                            return;
                        }

                        if (Strings.isNullOrEmpty(user.getPassword())){
                            user.setPassword(Hashing.sha256().hashString(UUID.randomUUID().toString(), StandardCharsets.UTF_8).toString());
                        }

                        userMapper.insertUser(user);
                    }else{
                        if (!Objects.equals(user.getLogin(), userMapper.getUser(user.getId()).getLogin())){
                            if (userMapper.getUser(user.getLogin()) != null){
                                login.error(getString("error_exist_login"));
                                target.add(feedback);
                                return;
                            }

                            userMapper.updateUserLogin(user);
                        }

                        if (!Strings.isNullOrEmpty(user.getPassword())){
                            userMapper.updateUserPassword(user);
                        }

                        if (user.getUserGroups() != null) {
                            userMapper.updateUserGroups(user);
                        }
                    }

                    //Worker
                    worker.setParentId(user.getId());
                    worker.setParentEntityId(User.ENTITY_ID);
                    worker.setUserId(getCurrentUser().getId());

                    worker.setNumber(Worker.LAST_NAME, nameService.getOrCreateLastName(lastName.getInput(), worker.getNumber(Worker.LAST_NAME)));
                    worker.setNumber(Worker.FIRST_NAME, nameService.getOrCreateFirstName(firstName.getInput(), worker.getNumber(Worker.FIRST_NAME)));
                    worker.setNumber(Worker.MIDDLE_NAME, nameService.getOrCreateMiddleName(middleName.getInput(), worker.getNumber(Worker.MIDDLE_NAME)));

                    if (participant && workerMapper.isExistJId(worker.getObjectId(), jId.getTextField().getInput())) {
                        jId.getTextField().error(getString("error_jid_exist"));
                        target.add(feedback, jId);
                        return;
                    }

                    if (manager != null) {
                        worker.setNumber(Worker.MANAGER_ID, manager.getObjectId());
                    }else{
                        worker.setNumber(Worker.MANAGER_ID, 1L);
                    }

                    if (worker.getObjectId() == null){
                        try {
                            domainMapper.insertDomain(worker);
                        } catch (Exception e) {
                            userMapper.deleteUser(user.getId());

                            throw e;
                        }

                        if (manager != null) {
                            domainNodeMapper.updateIndex(manager, worker);
                        }else{

                            domainNodeMapper.updateIndex(new Worker(1L, 1L, 2L, 0L), worker);
                        }

                        info(getString("info_user_created"));
                    }else{
                        boolean moveIndex = !Objects.equals(worker.getNumber(Worker.MANAGER_ID),
                                workerMapper.getWorker(worker.getObjectId()).getNumber(Worker.MANAGER_ID)); //todo opt

                        domainMapper.updateDomain(worker);

                        if (moveIndex){
                            workerService.moveIndex(manager, worker);

                            worker = workerMapper.getWorker(worker.getObjectId());
                            filterWrapper.getObject().setLeft(worker.getLeft());
                            filterWrapper.getObject().setRight(worker.getRight());
                            filterWrapper.getObject().setLevel(worker.getLevel());
                        }

                        info(getString("info_user_updated"));
                    }

                    target.add(feedback, structure, history);
                } catch (Exception e) {
                    error("Ошибка: " + e.getMessage());
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
                return worker.getObjectId() != null && participant;
            }
        }.setDefaultFormProcessing(false));

        boolean currentWorker = worker.getObjectId() != null && Objects.equals(getCurrentWorker().getObjectId(), worker.getObjectId());

        Link back = new Link<Void>("back") {
            @Override
            public void onClick() {
                if (backToWorkerList && (isAdmin() || isStructureAdmin())){
                    setResponsePage(WorkerListPage.class);
                }else{
                    PageParameters pageParameters = new PageParameters();

                    if (manager != null && manager.getObjectId() != null){
                        pageParameters.add("id", manager.getObjectId());
                    }

                    setResponsePage(WorkerPage.class, pageParameters);
                }
            }
        };
        back.add(new Label("label", getString("cancel")));

        form.add(back);

        List<IColumn<Worker, SortProperty>> columns = new ArrayList<>();

        columns.add(new DomainIdColumn<>());
        getEntityAttributes().forEach(a -> columns.add(new DomainColumn<>(a, entityService, domainService)));

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

        FilterDataTable<Worker> table = new FilterDataTable<Worker>("table", columns, dataProvider, form, 7){
            @Override
            public boolean isVisible() {
                return !showGraph.getObject();
            }

            @Override
            protected Item<Worker> newRowItem(String id, int index, final IModel<Worker> model) {
                Item<Worker> rowItem = super.newRowItem(id, index, model);

                rowItem.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        setResponsePage(WorkerPage.class, new PageParameters().add("id", model.getObject().getObjectId()));
                    }
                });

                rowItem.add(new CssClassNameAppender("pointer"));

                return rowItem;

            }
        };
        table.setHideOnEmpty(worker.getObjectId() == null);
        structure.add(table);

        filterWrapper.getMap().put("levelDepth", 1L);

        DropDownChoice levelDepth = new DropDownChoice<>("levelDepth", new PropertyModel<>(filterWrapper, "map.levelDepth"),
                LongStream.range(1, worker.getObjectId() != null ? workerMapper.getWorkerLevelDepth(worker.getObjectId()) + 1 : 2)
                        .boxed().collect(Collectors.toList()));
        levelDepth.setNullValid(false);
        levelDepth.add(OnChangeAjaxBehavior.onChange(target -> target.add(table)));
        structure.add(levelDepth );

        structure.add(new Link<Void>("structureLink") {
            @Override
            public void onClick() {
                setResponsePage(WorkerStructurePage.class, new PageParameters().add("id", worker.getObjectId())
                        .add("level_depth", filterWrapper.getMap().get("levelDepth")));
            }
        });
    }

    private List<EntityAttribute> getEntityAttributes() {
        Entity entity = entityMapper.getEntity(Worker.ENTITY_NAME);

        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Worker.LAST_NAME)
                .setReferenceEntityAttribute(entityService.getEntityAttribute(LastName.ENTITY_NAME, LastName.NAME)));

        list.add(entity.getEntityAttribute(Worker.FIRST_NAME)
                .setReferenceEntityAttribute(entityService.getEntityAttribute(FirstName.ENTITY_NAME, FirstName.NAME)));

        list.add(entity.getEntityAttribute(Worker.MIDDLE_NAME)
                .setReferenceEntityAttribute(entityService.getEntityAttribute(MiddleName.ENTITY_NAME, MiddleName.NAME)));

        list.add(entity.getEntityAttribute(Worker.J_ID));

        list.add(entity.getEntityAttribute(Worker.REGIONS)
                .setReferenceEntityAttribute(entityService.getEntityAttribute(Region.ENTITY_NAME, Region.NAME)));

        list.add(entity.getEntityAttribute(Worker.CITIES)
                .setReferenceEntityAttribute(entityService.getEntityAttribute(City.ENTITY_NAME, City.NAME))
                .setPrefixEntityAttribute(entityService.getEntityAttribute(City.ENTITY_NAME, City.CITY_TYPE)
                        .setReferenceEntityAttribute(entityService.getEntityAttribute(CityType.ENTITY_NAME, CityType.SHORT_NAME))));

        return list;
    }
}
