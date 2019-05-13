package ru.complitex.jedani.worker.page.worker;

import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackHeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.*;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.CityType;
import ru.complitex.address.entity.Region;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.component.DateTimeLabel;
import ru.complitex.common.wicket.datatable.*;
import ru.complitex.common.wicket.form.DateTextFieldFormGroup;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.common.wicket.form.FormGroupSelectPanel;
import ru.complitex.common.wicket.form.FormGroupTextField;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.common.wicket.util.Wickets;
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
import ru.complitex.domain.model.DateAttributeModel;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.model.TextAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.component.JedaniRoleSelectList;
import ru.complitex.jedani.worker.component.TypeSelect;
import ru.complitex.jedani.worker.component.WorkerAutoComplete;
import ru.complitex.jedani.worker.entity.MkStatus;
import ru.complitex.jedani.worker.entity.Position;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.entity.WorkerType;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

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

    public WorkerPage(PageParameters parameters) {
        Long id = parameters.get("id").toOptionalLong();

        boolean backToWorkerList = !parameters.get("wl").isNull();

        if (!parameters.get("new").isNull()){
            worker = new Worker();
            worker.init();

            worker.setType(WorkerType.PARTICIPANT);
            worker.setText(Worker.J_ID, workerMapper.getNewJId());

            if (id != null) {
                manager = workerMapper.getWorker(id);
            }else if (getCurrentWorker().getObjectId() != null){
                manager = workerMapper.getWorker(getCurrentWorker().getObjectId());
            }

            if (manager != null){
                manager.getNumberValues(Worker.REGIONS).forEach(n -> worker.addNumberValue(Worker.REGIONS, n));
                manager.getNumberValues(Worker.CITIES).forEach(n -> worker.addNumberValue(Worker.CITIES, n));

                worker.setManagerId(manager.getObjectId());
            }
        }else{
            if (id != null) {
                worker = workerMapper.getWorker(id);

                if (worker == null){
                    setResponsePage(getApplication().getHomePage());

                    getSession().error(getString("error_not_exist_worker"));

                    return;
                }
            } else {
                worker = getCurrentWorker();
            }

            if (worker.getManagerId() != null && worker.getManagerId() != 1L) {
                manager = workerMapper.getWorker(worker.getManagerId());
            }
        }

        if (worker.getObjectId() != null && !isAdmin()){
            if (getCurrentWorker().isRegionalLeader()){
                if (worker.getNumberValues(Worker.REGIONS).stream().noneMatch(r -> getCurrentWorker()
                        .getNumberValues(Worker.REGIONS).contains(r))){
                    throw new UnauthorizedInstantiationException(WorkerPage.class);
                }
            }else if (worker.isParticipant() && !isStructureAdmin()){
                if ((getCurrentWorker().getRight() < worker.getRight()) || (getCurrentWorker().getLeft() > worker.getLeft())){
                    throw new UnauthorizedInstantiationException(WorkerPage.class);
                }
            }
        }

        //User
        User user = worker.getParentId() != null
                ? userMapper.getUser(worker.getParentId())
                : new User(worker.getText(Worker.J_ID));

        if (user.getId() == null){
            user.addRole(JedaniRoles.USERS);
        }

        add(new Label("title", new ResourceModel("title")));

        FeedbackPanel feedback = new NotificationPanel("feedback").showRenderedMessages(false);
        feedback.setOutputMarkupId(true);
        add(feedback);

        //Data provider
        FilterWrapper<Worker> filterWrapper = newFilterWrapper();

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
        FilterDataForm<FilterWrapper<Worker>> form = new FilterDataForm<>("form", dataProvider);
        form.setOutputMarkupId(true);
        add(form);

        form.add(new FormGroupSelectPanel("type", new TypeSelect(FormGroupPanel.COMPONENT_ID,
                new NumberAttributeModel(worker, Worker.TYPE), WorkerType.PARTICIPANT, WorkerType.USER)
                .add(OnChangeAjaxBehavior.onChange(t -> {
                    if (!worker.isParticipant()){
                        worker.setJId(null);
                    }

                    t.add(form);
                }))){
            @Override
            public boolean isVisible() {
                return worker.getObjectId() == null && (isAdmin() || isStructureAdmin());
            }
        });

        DomainAutoCompleteFormGroup lastName, firstName, middleName;

        form.add(lastName = new DomainAutoCompleteFormGroup("lastName", LastName.ENTITY_NAME, LastName.NAME,
                new NumberAttributeModel(worker, Worker.LAST_NAME)).setInputRequired(true));
        form.add(firstName = new DomainAutoCompleteFormGroup("firstName", FirstName.ENTITY_NAME, FirstName.NAME,
                new NumberAttributeModel(worker, Worker.FIRST_NAME)).setInputRequired(true));
        form.add(middleName = new DomainAutoCompleteFormGroup("middleName", MiddleName.ENTITY_NAME, MiddleName.NAME,
                new NumberAttributeModel(worker, Worker.MIDDLE_NAME)));

        lastName.setEnabled(!isViewOnly());
        firstName.setEnabled(!isViewOnly());
        middleName.setEnabled(!isViewOnly());

        AttributeSelectFormGroup position;

        form.add(position = new AttributeSelectFormGroup("position", new NumberAttributeModel(worker, Worker.POSITION),
                Position.ENTITY_NAME, Position.NAME){
            @Override
            public boolean isEnabled() {
                return !isViewOnly() && (isAdmin() || (isStructureAdmin() && !Objects.equals(worker.getObjectId(),
                        getCurrentWorker().getObjectId())));
            }
        });

        FormGroupTextField<String> jId = new FormGroupTextField<String>("jId", new TextAttributeModel(worker, Worker.J_ID)){
            @Override
            public boolean isRequired() {
                return worker.isParticipant();
            }

            @Override
            public boolean isEnabled() {
                return isEditEnabled() && worker.isParticipant();
            }
        };

        if (worker.getObjectId() == null) {
            jId.onUpdate(target -> {
                if (worker.isParticipant() && workerMapper.isExistJId(null, jId.getTextField().getInput())) {
                    jId.getTextField().error(getString("error_jid_exist"));
                }

                target.add(jId);
            });
        }
        form.add(jId);

        form.add(new AttributeSelectFormGroup("mkStatus", new NumberAttributeModel(worker, Worker.MK_STATUS),
                MkStatus.ENTITY_NAME, MkStatus.NAME){
            @Override
            public boolean isVisible() {
                return worker.isParticipant();
            }

            @Override
            public boolean isEnabled() {
                return isEditEnabled();
            }
        });
        form.add(new DateTextFieldFormGroup("birthday", new DateAttributeModel(worker, Worker.BIRTHDAY))
                .setEnabled(!isViewOnly()));
        form.add(new AttributeInputListFormGroup("phone", Model.of(worker.getOrCreateAttribute(Worker.PHONE))){
            @Override
            public boolean isRequired() {
                return worker.isParticipant();
            }

            @Override
            public boolean isEnabled() {
                return !isViewOnly();
            }
        });
        form.add(new FormGroupTextField<>("email", new TextAttributeModel(worker, Worker.EMAIL, StringType.LOWER_CASE))
                .setEnabled(!isViewOnly()));

        AttributeSelectListFormGroup city, region;
        form.add(region = new AttributeSelectListFormGroup("region", Model.of(worker.getOrCreateAttribute(Worker.REGIONS)),
                Region.ENTITY_NAME, Region.NAME, true){
            @Override
            public boolean isRequired() {
                return worker.isParticipant();
            }

            @Override
            public boolean isEnabled() {
                return isEditEnabled();
            }
        });
        form.add(city = new AttributeSelectListFormGroup("city", Model.of(worker.getOrCreateAttribute(Worker.CITIES)),
                City.ENTITY_NAME, City.NAME, region.getListModel(), true){
            @Override
            protected String getPrefix(Domain domain) {
                Long cityTypeId = domain.getNumber(City.CITY_TYPE);

                if (cityTypeId != null){
                    Domain cityType = domainMapper.getDomain(CityType.ENTITY_NAME, cityTypeId);

                    if (cityType != null){
                        return cityType.getTextValue(CityType.SHORT_NAME).toLowerCase() + " ";
                    }
                }

                return super.getPrefix(domain);
            }

            @Override
            public boolean isRequired() {
                return worker.isParticipant();
            }

            @Override
            public boolean isEnabled() {
                return isEditEnabled();
            }
        });
        region.onChange(t -> t.add(city));

        //Roles

        List<String> roles = new ArrayList<>();

        if (isAdmin()){
            roles.add(JedaniRoles.ADMINISTRATORS);
            roles.add(JedaniRoles.STRUCTURE_ADMINISTRATORS);
            roles.add(JedaniRoles.PROMOTION_ADMINISTRATORS);
            roles.add(JedaniRoles.USERS);
        }else if (isStructureAdmin()){
            roles.add(JedaniRoles.STRUCTURE_ADMINISTRATORS);
            roles.add(JedaniRoles.PROMOTION_ADMINISTRATORS);
            roles.add(JedaniRoles.USERS);
        }

        IModel<List<String>> userRolesModel = new ListModel<>(user.getRoles());

        form.add(new FormGroupPanel("role", new JedaniRoleSelectList(FormGroupPanel.COMPONENT_ID,
                userRolesModel, roles){
            @Override
            protected void onChange(AjaxRequestTarget target) {
                user.setRoles(userRolesModel.getObject());

                if (!user.hasRole(JedaniRoles.ADMINISTRATORS) && !user.hasRole(JedaniRoles.STRUCTURE_ADMINISTRATORS)){
                    worker.setNumber(Worker.POSITION, null);
                }

                target.add(position);
            }
        }).setVisible(isAdmin() || isStructureAdmin()));

        TextField<String> login = new TextField<>("userLogin", new PropertyModel<>(user, "login"));
        login.setRequired(true);
        login.setEnabled(!isViewOnly());
        form.add(login);

        PasswordTextField password = new PasswordTextField("userPassword", new PropertyModel<>(user, "password"));
        password.setRequired(false);
        password.setVisible(!isViewOnly());
        form.add(password);

        PasswordTextField confirmPassword = new PasswordTextField("confirmPassword", new PropertyModel<>(user, "confirmPassword"));
        confirmPassword.setRequired(false);
        confirmPassword.setVisible(!isViewOnly());
        form.add(confirmPassword);

        form.add(new DateTextFieldFormGroup("registrationDate", new DateAttributeModel(worker, Worker.INVOLVED_AT)){
            @Override
            public boolean isRequired() {
                return worker.isParticipant();
            }

            @Override
            public boolean isEnabled() {
                return isEditEnabled();
            }
        }.onUpdate(t -> t.add(get("form:registrationDate"))));

        //Manager
        WebMarkupContainer managerContainer = new WebMarkupContainer("manager"){
            @Override
            public boolean isVisible() {
                return worker.isParticipant();
            }

            @Override
            public boolean isEnabled() {
                return isEditEnabled();
            }
        };
        form.add(managerContainer);

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

        managerContainer.add(new WorkerAutoComplete("managerFio",
                new PropertyModel<>(worker.getOrCreateAttribute(Worker.MANAGER_ID), "number"),
                target -> {
                    manager = workerMapper.getWorker(worker.getNumber(Worker.MANAGER_ID));

                    target.add(managerPhone, managerEmail);
                }));

        //Structure
        WebMarkupContainer structure = new WebMarkupContainer("structure"){
            @Override
            public boolean isVisible() {
                return worker.getObjectId() != null && worker.isParticipant() && dataProvider.size() > 0;
            }
        };

        structure.setOutputMarkupId(true);
        form.add(structure);

        //History
        WebMarkupContainer historyHeader = new WebMarkupContainer("historyHeader"){
            @Override
            public boolean isVisible() {
                return worker.getObjectId() != null && (isAdmin() || isStructureAdmin());
            }
        };
        form.add(historyHeader);

        Entity workerEntity = entityMapper.getEntity(Worker.ENTITY_NAME);

        List<IColumn<Attribute, String>> historyColumns = new ArrayList<>();

        historyColumns.add(new AbstractColumn<Attribute, String>(new ResourceModel("date"), "date") {
            @Override
            public void populateItem(Item<ICellPopulator<Attribute>> cellItem, String componentId, IModel<Attribute> rowModel) {
                cellItem.add(new DateTimeLabel(componentId, rowModel.getObject().getStartDate()));
            }
        });
        historyColumns.add(new AbstractColumn<Attribute, String>(new ResourceModel("name"), "name") {
            @Override
            public void populateItem(Item<ICellPopulator<Attribute>> cellItem, String componentId, IModel<Attribute> rowModel) {
                EntityAttribute entityAttribute = workerEntity.getEntityAttribute(rowModel.getObject().getEntityAttributeId());

                cellItem.add(new Label(componentId, entityAttribute.getValue() != null
                        ? entityAttribute.getValue().getText()
                        : entityAttribute.getEntityAttributeId().toString()));
            }
        });
        historyColumns.add(new AbstractColumn<Attribute, String>(new ResourceModel("value"), "value") {
            @Override
            public void populateItem(Item<ICellPopulator<Attribute>> cellItem, String componentId, IModel<Attribute> rowModel) {
                Attribute attribute = rowModel.getObject();

                EntityAttribute entityAttribute = workerEntity.getEntityAttribute(attribute.getEntityAttributeId());

                String value = "";

                switch (entityAttribute.getValueType()){
                    case TEXT_VALUE:
                        value = attribute.getValues().stream()
                                .map(Value::getText)
                                .collect(Collectors.joining(","));

                        break;
                    case NUMBER_VALUE:
                    case ENTITY_VALUE:
                        value = attribute.getValues().stream()
                                .map(v -> String.valueOf(v.getNumber()))
                                .collect(Collectors.joining(","));

                        break;
                    case TEXT:
                    case DECIMAL:
                        value = attribute.getText();

                        break;
                    case BOOLEAN:
                    case NUMBER:
                    case ENTITY:
                        value = String.valueOf(attribute.getNumber());

                        break;
                    case DATE:
                        value = String.valueOf(attribute.getDate());

                        break;
                }

                cellItem.add(new Label(componentId, value));
            }
        });

        SortableDataProvider<Attribute, String> historyDataProvider = new SortableDataProvider<Attribute, String>() {
            @Override
            public Iterator<? extends Attribute> iterator(long first, long count) {
                return attributeMapper.getHistoryAttributes(FilterWrapper.of(new Attribute(Worker.ENTITY_NAME)
                        .setObjectId(worker.getObjectId()), first, count)).iterator();
            }

            @Override
            public long size() {
                return attributeMapper.getHistoryAttributesCount(FilterWrapper.of(new Attribute(Worker.ENTITY_NAME)
                        .setObjectId(worker.getObjectId())));
            }

            @Override
            public IModel<Attribute> model(Attribute object) {
                return Model.of(object);
            }
        };

        DataTable<Attribute, String> historyDataTable = new DataTable<Attribute, String>("history", historyColumns,
                historyDataProvider, 5){
            @Override
            public boolean isVisible() {
                return worker.getObjectId() != null;
            }
        };
        historyDataTable.addTopToolbar(new AjaxFallbackHeadersToolbar<>(historyDataTable, historyDataProvider));
        historyDataTable.addBottomToolbar(new NavigationToolbar(historyDataTable, "workerPage_history"));
        historyDataTable.setOutputMarkupId(true);
        form.add(historyDataTable);

        form.add(new IndicatingAjaxButton("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    if (!worker.isParticipant()){
                        worker.setManagerId(null);
                        worker.setJId(null);
                    }

                    //User
                    if (!Strings.isNullOrEmpty(user.getPassword())){
                        if (!user.getPassword().equals(user.getConfirmPassword())){
                            password.error(getString("error_confirm_password"));
                            target.add(feedback);
                            return;
                        }

                        user.setPassword(Hashing.sha256().hashString(user.getPassword(), StandardCharsets.UTF_8).toString());
                    }

                    user.setRoles(userRolesModel.getObject());

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

                    worker.setLastNameId(nameService.getOrCreateLastName(lastName.getInput(), worker.getLastNameId()));
                    worker.setFirstNameId(nameService.getOrCreateFirstName(firstName.getInput(), worker.getFistNameId()));
                    worker.setMiddleNameId(nameService.getOrCreateMiddleName(middleName.getInput(), worker.getMiddleNameId()));

                    lastName.detachModels();
                    firstName.detachModels();
                    middleName.detachModels();

                    if (worker.isParticipant() && workerMapper.isExistJId(worker.getObjectId(), jId.getTextField().getInput())) {
                        jId.getTextField().error(getString("error_jid_exist"));
                        target.add(feedback, jId);
                        return;
                    }

                    if (worker.getManagerId() == null){
                        worker.setNumber(Worker.MANAGER_ID, 1L);
                        manager = workerMapper.getWorker(1L);
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

                        success(getString(worker.isParticipant() ? "info_worker_created" : "info_user_created"));
                    }else{
                        boolean moveIndex = !Objects.equals(worker.getManagerId(),
                                workerMapper.getWorker(worker.getObjectId()).getManagerId()); //todo opt

                        domainMapper.updateDomain(worker);

                        if (moveIndex){
                            workerService.moveIndex(manager, worker);

                            worker = workerMapper.getWorker(worker.getObjectId());
                            filterWrapper.getObject().setLeft(worker.getLeft());
                            filterWrapper.getObject().setRight(worker.getRight());
                            filterWrapper.getObject().setLevel(worker.getLevel());
                        }

                        success(getString("info_updated"));
                    }

                    target.add(feedback, form);
                } catch (Exception e) {
                    if (worker.getObjectId() == null && user.getId() != null){
                        try {
                            userMapper.deleteUser(user.getId());
                        } catch (Exception ex) {
                            log.error("error save worker ", ex);
                        }
                    }

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
                        target.add(Wickets.getAjaxParent(object));
                    }
                });

                target.add(form);
            }

            @Override
            public boolean isVisible() {
                return !isViewOnly();
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
                return !isViewOnly() && worker.getObjectId() != null && worker.isParticipant();
            }
        }.setDefaultFormProcessing(false));

        Link back = new Link<Void>("back") {
            @Override
            public void onClick() {
                if (backToWorkerList && (isAdmin() || isStructureAdmin())){
                    setResponsePage(WorkerListPage.class);
                }else{
                    PageParameters pageParameters = new PageParameters();

                    if (manager != null && manager.getObjectId() != null && !isCurrentWorkerPage()){
                        pageParameters.add("id", manager.getObjectId());
                    }

                    setResponsePage(WorkerPage.this.getClass(), pageParameters);
                }
            }

            @Override
            public boolean isVisible() {
                return !isViewOnly();
            }
        };
        back.add(new Label("label", getString("cancel")));

        form.add(back);

        List<IColumn<Worker, SortProperty>> columns = new ArrayList<>();

        columns.add(new DomainIdColumn<>());
        getEntityAttributes().forEach(a -> columns.add(new DomainColumn<>(a)));

        //noinspection Duplicates
        columns.add(new AbstractDomainColumn<Worker>(new ResourceModel("subWorkersCount"),
                new SortProperty("subWorkersCount")) {
            @Override
            public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getSubWorkerCount()));
            }

            @Override
            public Component getFilter(String componentId, FilterDataForm<?> form) {
                return new TextDataFilter<>(componentId, Model.of(""), form);
            }
        });

        //noinspection Duplicates
        columns.add(new AbstractDomainColumn<Worker>(new ResourceModel("level"), new SortProperty("level")) {
            @Override
            public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getLevel()));
            }

            @Override
            public Component getFilter(String componentId, FilterDataForm<?> form) {
                return new TextDataFilter<>(componentId, Model.of(""), form);
            }
        });

        WorkerRemoveModal workerRemoveModal = new WorkerRemoveModal("workerRemove"){
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(feedback, structure);
            }
        };
        structure.add(workerRemoveModal);

        //noinspection Duplicates
        columns.add(new DomainActionColumn<Worker>(WorkerPage.class){
            @Override
            public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                PageParameters pageParameters = new PageParameters();
                pageParameters.add("id", rowModel.getObject().getObjectId());

                RepeatingView repeatingView = new RepeatingView(componentId);
                cellItem.add(repeatingView);

                repeatingView.add(new LinkPanel(repeatingView.newChildId(), new BootstrapBookmarkablePageLink<>(LinkPanel.LINK_COMPONENT_ID,
                        WorkerPage.this.getClass(), pageParameters, Buttons.Type.Link).setIconType(GlyphIconType.edit)));

                if (!isViewOnly()) {
                    repeatingView.add(new LinkPanel(repeatingView.newChildId(), new BootstrapAjaxLink<Worker>(LinkPanel.LINK_COMPONENT_ID,
                            Buttons.Type.Link) {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            workerRemoveModal.delete(target, rowModel.getObject());
                        }

                        @Override
                        protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                            super.updateAjaxAttributes(attributes);

                            attributes.setEventPropagation(AjaxRequestAttributes.EventPropagation.STOP);
                        }

                        @Override
                        public boolean isVisible() {
                            return !Objects.equals(rowModel.getObject().getObjectId(), 1L) &&
                                    !Objects.equals(rowModel.getObject().getObjectId(), getCurrentWorker().getObjectId());
                        }
                    }.setIconType(GlyphIconType.remove)));
                }
            }

            @Override
            public String getCssClass() {
                return "domain-id-column" + (!isViewOnly() ? " worker-action" : "");
            }
        });

        FilterDataTable<Worker> table = new FilterDataTable<Worker>("table", columns, dataProvider, form, 7, "workerPage"){
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
                        PageParameters pageParameters = new PageParameters();
                        pageParameters.add("id", model.getObject().getObjectId());

                        setResponsePage(WorkerPage.this.getClass(), new PageParameters().add("id", model.getObject().getObjectId()));
                    }
                });

                rowItem.add(new CssClassNameAppender("pointer"));

                return rowItem;

            }
        };
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
                        .add("level", filterWrapper.getMap().get("levelDepth")));
            }
        });
    }

    protected FilterWrapper<Worker> newFilterWrapper() {
        return FilterWrapper.of(new Worker(worker.getLeft(), worker.getRight(), worker.getLevel()));
    }

    @SuppressWarnings("Duplicates")
    private List<EntityAttribute> getEntityAttributes() {
        Entity entity = entityMapper.getEntity(Worker.ENTITY_NAME);

        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Worker.LAST_NAME).withReference(LastName.ENTITY_NAME, LastName.NAME));
        list.add(entity.getEntityAttribute(Worker.FIRST_NAME).withReference(FirstName.ENTITY_NAME, FirstName.NAME));
        list.add(entity.getEntityAttribute(Worker.MIDDLE_NAME).withReference(MiddleName.ENTITY_NAME, MiddleName.NAME));

        list.add(entity.getEntityAttribute(Worker.J_ID));

        list.add(entity.getEntityAttribute(Worker.REGIONS).withReference(Region.ENTITY_NAME, Region.NAME));

        list.add(entity.getEntityAttribute(Worker.CITIES).withReference(City.ENTITY_NAME, City.NAME)
                .setPrefixEntityAttribute(entityService.getEntityAttribute(City.ENTITY_NAME, City.CITY_TYPE)
                        .withReference(CityType.ENTITY_NAME, CityType.SHORT_NAME)));

        return list;
    }

    protected boolean isCurrentWorkerPage(){
        return Objects.equals(worker.getObjectId(), getCurrentWorker().getObjectId());
    }

    protected boolean isEditEnabled(){
        return isAdmin() || isStructureAdmin() || (isUser() && !isCurrentWorkerPage());
    }

    protected boolean isViewOnly(){
        return false;
    }

    protected Worker getWorker(){
        return worker;
    }
}
