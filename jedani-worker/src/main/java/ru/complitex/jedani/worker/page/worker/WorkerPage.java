package ru.complitex.jedani.worker.page.worker;

import com.google.common.base.Strings;
import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxBootstrapTabbedPanel;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.*;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.resource.FileSystemResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.CityType;
import ru.complitex.address.entity.Region;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.Sort;
import ru.complitex.common.util.Dates;
import ru.complitex.common.util.Images;
import ru.complitex.common.wicket.component.DateTimeLabel;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.common.wicket.form.FormGroupSelectPanel;
import ru.complitex.common.wicket.form.FormGroupTextField;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.common.wicket.table.*;
import ru.complitex.common.wicket.util.Wickets;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.component.datatable.DomainActionColumn;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.component.datatable.DomainIdColumn;
import ru.complitex.domain.component.form.FormGroupAttributeInputList;
import ru.complitex.domain.component.form.FormGroupAttributeSelect;
import ru.complitex.domain.component.form.FormGroupDomainAutoComplete;
import ru.complitex.domain.entity.*;
import ru.complitex.domain.mapper.EntityMapper;
import ru.complitex.domain.model.DateAttributeModel;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.model.TextAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.domain.util.Domains;
import ru.complitex.jedani.worker.component.JedaniRoleSelectList;
import ru.complitex.jedani.worker.component.PeriodPanel;
import ru.complitex.jedani.worker.component.TypeSelect;
import ru.complitex.jedani.worker.component.WorkerAutoComplete;
import ru.complitex.jedani.worker.entity.Currency;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.page.payment.PaymentPanel;
import ru.complitex.jedani.worker.page.reward.RewardPanel;
import ru.complitex.jedani.worker.page.sale.SalePanel;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.jedani.worker.service.*;
import ru.complitex.name.entity.FirstName;
import ru.complitex.name.entity.LastName;
import ru.complitex.name.entity.MiddleName;
import ru.complitex.name.service.NameService;
import ru.complitex.user.entity.User;
import ru.complitex.user.mapper.UserMapper;

import javax.inject.Inject;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 5:57
 */

@AuthorizeInstantiation(JedaniRoles.AUTHORIZED)
public class WorkerPage extends BasePage {
    private final Logger log = LoggerFactory.getLogger(Worker.class);

    public final static String PHOTO_FILE_PREFIX = "photo_";

    @Inject
    private EntityMapper entityMapper;

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
    private WorkerNodeService workerNodeService;

    @Inject
    private CardService cardService;

    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private RewardService rewardService;

    @Inject
    private AccountService accountService;

    private Worker worker;
    private Worker manager;

    private String photoDir;
    private FileUploadField photoUploadFiled;

    public WorkerPage(PageParameters parameters) {
        Long id = parameters.get("id").toOptionalLong();

        if (!parameters.get("new").isNull()){
            worker = new Worker();

            worker.setType(WorkerType.PK);
            worker.setText(Worker.J_ID, workerMapper.getNewJId());

            if (id != null) {
                manager = workerMapper.getWorker(id);
            }else if (getCurrentWorker().getObjectId() != null){
                manager = workerMapper.getWorker(getCurrentWorker().getObjectId());
            }

            if (manager != null){
                worker.setManagerId(manager.getObjectId());
                worker.setCityId(manager.getCityId());
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
                City currentCity = domainService.getDomain(City.class, getCurrentWorker().getCityId());
                City city = domainService.getDomain(City.class, worker.getCityId());

                if (!Objects.equals(currentCity.getParentId(), city.getParentId())){
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

        Provider<Worker> provider = new Provider<>(filterWrapper) {

            @Override
            public List<Worker> getList() {
                return workerMapper.getWorkers(filterWrapper);
            }

            @Override
            public Long getCount() {
                return workerMapper.getWorkersCount(getFilterState());
            }
        };

        //Worker
        FilterForm<FilterWrapper<Worker>> form = new FilterForm<>("form", provider);
        add(form);

        form.add(new FormGroupSelectPanel("type", new TypeSelect(FormGroupPanel.COMPONENT_ID,
                new NumberAttributeModel(worker, Worker.TYPE), WorkerType.PK, WorkerType.USER)
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

        //Photo

        Setting photoSetting = domainService.getDomain(Setting.class, Setting.PHOTO_ID);

        photoDir = photoSetting.getText(Setting.VALUE);

        form.add(new NonCachingImage("photo", new FileSystemResource(new File(photoDir,
                PHOTO_FILE_PREFIX + worker.getObjectId()).toPath())){
            @Override
            public boolean isVisible() {
                return Files.exists(new File(photoDir,PHOTO_FILE_PREFIX + worker.getObjectId()).toPath());
            }
        });

        photoUploadFiled = new FileUploadField("photoUploadFiled");
        form.add(photoUploadFiled);


        FormGroupDomainAutoComplete<LastName> lastName;
        FormGroupDomainAutoComplete<FirstName> firstName;
        FormGroupDomainAutoComplete<MiddleName> middleName;

        form.add(lastName = new FormGroupDomainAutoComplete<>("lastName", LastName.class, LastName.NAME,
                new NumberAttributeModel(worker, Worker.LAST_NAME)).setInputRequired(true));
        form.add(firstName = new FormGroupDomainAutoComplete<>("firstName", FirstName.class, FirstName.NAME,
                new NumberAttributeModel(worker, Worker.FIRST_NAME)).setInputRequired(true));
        form.add(middleName = new FormGroupDomainAutoComplete<>("middleName", MiddleName.class, MiddleName.NAME,
                new NumberAttributeModel(worker, Worker.MIDDLE_NAME)));

        lastName.setEnabled(!isViewOnly());
        firstName.setEnabled(!isViewOnly());
        middleName.setEnabled(!isViewOnly());

        FormGroupAttributeSelect position;

        form.add(position = new FormGroupAttributeSelect("position", new NumberAttributeModel(worker, Worker.POSITION),
                Position.ENTITY_NAME, Position.NAME){
            @Override
            public boolean isEnabled() {
                return !isViewOnly() && (isAdmin() || (isStructureAdmin() && !Objects.equals(worker.getObjectId(),
                        getCurrentWorker().getObjectId())));
            }
        }.setNullValid(true));

        FormGroupTextField<String> jId = new FormGroupTextField<>("jId", new TextAttributeModel(worker, Worker.J_ID)){
            @Override
            public boolean isRequired() {
                return worker.isParticipant();
            }

            @Override
            public boolean isEnabled() {
                return worker.getObjectId() != null && isEditEnabled() && worker.isParticipant() &&
                        (isAdmin() || isStructureAdmin());
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

        form.add(new FormGroupAttributeSelect("mkStatus", new NumberAttributeModel(worker, Worker.MK_STATUS),
                MkStatus.ENTITY_NAME, MkStatus.NAME){
            @Override
            public boolean isVisible() {
                return worker.isParticipant();
            }

            @Override
            public boolean isEnabled() {
                return isEditEnabled();
            }
        }.setRequired(true).setNullValid(false));

        form.add(new FormGroupDateTextField("birthday", new DateAttributeModel(worker, Worker.BIRTHDAY))
                .setEnabled(!isViewOnly()));

        form.add(new FormGroupAttributeInputList("phone", Model.of(worker.getOrCreateAttribute(Worker.PHONE))){
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
                .setRequired(true)
                .setEnabled(!isViewOnly()));


        Component region;

        IModel<Long> regionModel = new Model<>();

        if (worker.getCityId() != null) {
            regionModel.setObject(domainService.getParentId(City.ENTITY_NAME, worker.getCityId()));
        }

        form.add(region = new FormGroupDomainAutoComplete<>("region", Region.class, Region.NAME, regionModel));

        form.add(new FormGroupDomainAutoComplete<>("city", City.class, City.NAME,
                new NumberAttributeModel(worker.getOrCreateAttribute(Worker.CITY))) {
            @Override
            public String getTextValue(City domain) {
                String cityType = "";

                if (domain.getCityTypeId() != null){
                    cityType = domainService.getTextValue(CityType.ENTITY_NAME, domain.getCityTypeId(), CityType.SHORT_NAME).toLowerCase();
                }

                String city = Attributes.capitalize(domain.getName());

                return cityType + " " + city;
            }

            @Override
            protected City getFilterObject(String input) {
                City city = Domains.newObject(City.class);

                city.setParentId(regionModel.getObject());

                city.getOrCreateAttribute(City.NAME).setText(input);

                return city;
            }

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                regionModel.setObject(domainService.getParentId(City.ENTITY_NAME, worker.getCityId()));

                target.add(region);
            }
        }.setRequired(true));

        List<String> roles = new ArrayList<>();

        if (isAdmin()){
            roles.add(JedaniRoles.ADMINISTRATORS);
            roles.add(JedaniRoles.STRUCTURE_ADMINISTRATORS);
            roles.add(JedaniRoles.PROMOTION_ADMINISTRATORS);
            roles.add(JedaniRoles.SALE_ADMINISTRATORS);
            roles.add(JedaniRoles.PAYMENT_ADMINISTRATORS);
            roles.add(JedaniRoles.PAYOUT_ADMINISTRATORS);
            roles.add(JedaniRoles.SITE_DOCS);
            roles.add(JedaniRoles.USERS);
        }else if (isStructureAdmin()){
            roles.add(JedaniRoles.STRUCTURE_ADMINISTRATORS);
            roles.add(JedaniRoles.PROMOTION_ADMINISTRATORS);
            roles.add(JedaniRoles.SALE_ADMINISTRATORS);
            roles.add(JedaniRoles.PAYMENT_ADMINISTRATORS);
            roles.add(JedaniRoles.PAYOUT_ADMINISTRATORS);
            roles.add(JedaniRoles.SITE_DOCS);
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
        login.setEnabled(worker.getObjectId() == null || (!isViewOnly() && (isAdmin() || isStructureAdmin())));
        form.add(login);

        PasswordTextField password = new PasswordTextField("userPassword", new PropertyModel<>(user, "password"));
        password.setRequired(false);
        password.setVisible(!isViewOnly());
        form.add(password);

        PasswordTextField confirmPassword = new PasswordTextField("confirmPassword", new PropertyModel<>(user, "confirmPassword"));
        confirmPassword.setRequired(false);
        confirmPassword.setVisible(!isViewOnly());
        form.add(confirmPassword);


        FormGroupTextField<String> card = new FormGroupTextField<>("card", new Model<>()){
            @Override
            public boolean isVisible() {
                return worker.isParticipant();
            }

            @Override
            public boolean isRequired() {
                return (worker.getObjectId() == null && worker.isParticipant());
            }
        };


        if (worker.getObjectId() != null) {
            Card cardObject = cardService.getCardByWorker(worker.getObjectId());

            if (cardObject != null){
                card.getTextField().setModelObject(cardObject.getNumber());

                card.setEnabled(false);
            }
        }

        form.add(card);

        form.add(new FormGroupDateTextField("registrationDate", new DateAttributeModel(worker, Worker.REGISTRATION_DATE)){
            @Override
            public boolean isRequired() {
                return worker.isParticipant();
            }

            @Override
            public boolean isEnabled() {
                return isEditEnabled();
            }
        }.onUpdate(t -> t.add(get("form:registrationDate"))));


        WebMarkupContainer managerContainer = new WebMarkupContainer("manager"){
            @Override
            public boolean isVisible() {
                return worker.isParticipant() && (worker.getObjectId() == null || worker.getObjectId() > 2L);
            }

            @Override
            public boolean isEnabled() {
                return isEditEnabled();
            }
        };
        form.add(managerContainer);

        managerContainer.add(new AjaxLink<Worker>("managerLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (manager != null) {
                    setResponsePage(WorkerPage.class, new PageParameters().add("id", manager.getObjectId()));
                }
            }

            @Override
            public boolean isVisible() {
                return (isAdmin() || isStructureAdmin()) && manager != null;
            }
        });

        Label managerPhone = new Label("managerPhones", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return manager != null ? String.join(", ", manager.getTextValues(Worker.PHONE)) : null;
            }
        }){
            @Override
            public boolean isVisible() {
                return getDefaultModelObject() != null;
            }
        };
        managerPhone.setOutputMarkupId(true);
        managerContainer.add(managerPhone);

        Label managerEmail = new Label("managerEmail", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return manager != null ? StringUtils.lowerCase(manager.getText(Worker.EMAIL)): null;
            }
        }){
            @Override
            public boolean isVisible() {
                return getDefaultModelObject() != null;
            }
        };
        managerEmail.setOutputMarkupId(true);
        managerContainer.add(managerEmail);

        managerContainer.add(new WorkerAutoComplete("managerFio",
                new PropertyModel<>(worker.getOrCreateAttribute(Worker.MANAGER), "number"){
                    @Override
                    public void setObject(Long object) {
                        super.setObject(object);

                        manager = workerMapper.getWorker(worker.getNumber(Worker.MANAGER));
                    }
                }){
            @Override
            protected Long getWorkerType() {
                return null;
            }
        }.onChange(t -> t.add(managerPhone, managerEmail)));

        form.add(new IndicatingAjaxButton("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    if (worker.isParticipant() && worker.getManagerId() == null){
                        error(getString("error_manager_is_empty"));
                        target.add(feedback);
                        return;
                    }

                    if (worker.isParticipant()) {
                        Worker manager = workerMapper.getWorker(worker.getManagerId());

                        if (manager.getLeft() > worker.getLeft() && manager.getRight() < worker.getRight()) {
                            error(getString("error_manager_worker_structure"));
                            target.add(feedback);
                            return;
                        }
                    }

                    if (!worker.isParticipant()){
                        worker.setManagerId(null);
                        worker.setJId(null);
                    }

                    //Card

                    String cardNumber = card.getTextField().getModelObject();
                    Card cardObject = null;

                    if (cardNumber != null){
                        if (!cardService.isValid(cardNumber)){
                            card.error(getString("error_card_is_not_valid"));
                            target.add(feedback);
                            return;
                        }

                        cardObject = cardService.getCardByNumber(cardNumber);

                        if (cardObject == null){
                            card.error(getString("error_card_is_not_exists"));
                            target.add(feedback);
                            return;
                        }else if (cardObject.getWorkerId() != null &&
                                !Objects.equals(worker.getObjectId(), cardObject.getWorkerId())){
                            card.error(getString("error_card_is_not_same_worker"));
                            target.add(feedback);
                            return;
                        }
                    }

                    //User
                    if (!Strings.isNullOrEmpty(user.getPassword())){
                        if (!user.getPassword().equals(user.getConfirmPassword())){
                            password.error(getString("error_confirm_password"));
                            target.add(feedback);
                            return;
                        }

                        user.setPassword(ru.complitex.common.util.Strings.sha256(user.getPassword()));
                    }

                    user.setRoles(userRolesModel.getObject());

                    Long currentWorkerId = getCurrentWorker().getObjectId();

                    if (user.getId() == null){
                        if (userMapper.getUser(user.getLogin()) != null){
                            login.error(getString("error_login_exist"));
                            target.add(feedback);
                            return;
                        }

                        if (Strings.isNullOrEmpty(user.getPassword())){
                            user.setPassword(ru.complitex.common.util.Strings.sha256(UUID.randomUUID().toString()));
                        }

                        workerService.save(user, currentWorkerId);
                    }else{
                        if (!Objects.equals(user.getLogin(), userMapper.getUser(user.getId()).getLogin())){
                            if (userMapper.getUser(user.getLogin()) != null){
                                login.error(getString("error_exist_login"));
                                target.add(feedback);
                                return;
                            }

                            workerService.updateUserLogin(user, currentWorkerId);
                        }

                        if (!Strings.isNullOrEmpty(user.getPassword())){
                            workerService.updateUserPassword(user, currentWorkerId);
                        }

                        if (user.getUserGroups() != null) {
                            workerService.updateUserGroups(user, currentWorkerId);
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

                    if (worker.getManagerId() == null) {
                        worker.setNumber(Worker.MANAGER, 1L);
                        manager = workerMapper.getWorker(1L);
                    }

                    if (worker.getObjectId() == null){
                        try {
                            domainService.insert(worker);

                            if (cardNumber != null){
                                cardObject.setWorkerId(worker.getObjectId());

                                cardService.save(cardObject);
                            }
                        } catch (Exception e) {
                            userMapper.deleteUser(user);

                            throw e;
                        }

                        workerNodeService.updateIndex(worker);

                        success(getString(worker.isParticipant() ? "info_worker_created" : "info_user_created"));
                    }else{
                        boolean moveIndex = !Objects.equals(worker.getManagerId(),
                                domainService.getNumber(Worker.ENTITY_NAME, worker.getObjectId(), Worker.MANAGER));

                        if (moveIndex){
                            worker.setWorkerStatus(WorkerStatus.MANAGER_CHANGED);
                        }

                        if ("jedani".equals(user.getConfirmPassword())) {
                            worker.setEndDate(null);
                            worker.setStatus(Status.ACTIVE);
                            worker.setWorkerStatus(null);
                        }

                        domainService.update(worker);

                        if (moveIndex){
                            workerNodeService.moveIndex(worker);

                            worker = workerMapper.getWorker(worker.getObjectId());

                            filterWrapper.getObject().setLeft(worker.getLeft());
                            filterWrapper.getObject().setRight(worker.getRight());
                            filterWrapper.getObject().setLevel(worker.getLevel());
                        }

                        if (cardNumber != null && card.isEnabled()) {
                            cardObject.setWorkerId(worker.getObjectId());

                            cardService.save(cardObject);
                        }

                        success(getString("info_updated"));
                    }

                    //photo

                    FileUpload photoFileUpload = photoUploadFiled.getFileUpload();

                    if (photoFileUpload != null) {
                        if (photoFileUpload.getSize() <= 314572800){
                            try {
                                File photoFileDir = new File(photoDir);

                                File photoFile = new File(photoFileDir, PHOTO_FILE_PREFIX + worker.getObjectId());

                                if (Files.exists(photoFile.toPath())){
                                    Files.move(photoFile.toPath(), new File(photoFile.getParentFile(), PHOTO_FILE_PREFIX +
                                            worker.getObjectId() + "_deleted_" +
                                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))).toPath());
                                }

                                Images.write(photoUploadFiled.getFileUpload().getInputStream(), 1920, 1080,
                                        FilenameUtils.getExtension(photoUploadFiled.getFileUpload().getClientFileName()), photoFile);
                            } catch (Exception e) {
                                error(getString("error_photo_load"));
                            }
                        }else{
                            error(getString("error_photo_size"));
                        }
                    }

                    target.add(feedback, form);
                } catch (Exception e) {
                    if (worker.getObjectId() == null && user.getId() != null){
                        try {
                            userMapper.deleteUser(user);
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

        List<ITab> tabs = new ArrayList<>();

        tabs.add(new AbstractTab(new StringResourceModel("structure", WorkerPage.this)) {
            @Override
            public WebMarkupContainer getPanel(String panelId) {
                Fragment structure = new Fragment(panelId, "structure", WorkerPage.this);

                WorkerRemoveModal workerRemoveModal = new WorkerRemoveModal("workerRemove"){
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        target.add(feedback, structure);
                    }
                };
                structure.add(workerRemoveModal);

                List<IColumn<Worker, Sort>> columns = new ArrayList<>();

                columns.add(new DomainIdColumn<>());

                Entity entity = entityMapper.getEntity(Worker.ENTITY_NAME);

                columns.add(new DomainColumn<>(entity.getEntityAttribute(Worker.J_ID)));
                columns.add(new DomainColumn<>(entity.getEntityAttribute(Worker.LAST_NAME).withReference(LastName.class, LastName.NAME)));
                columns.add(new DomainColumn<>(entity.getEntityAttribute(Worker.FIRST_NAME).withReference(FirstName.class, FirstName.NAME)));
                columns.add(new DomainColumn<>(entity.getEntityAttribute(Worker.MIDDLE_NAME).withReference(MiddleName.class, MiddleName.NAME)));
                columns.add(new DomainColumn<>(entity.getEntityAttribute(Worker.CITY).withReference(City.class, City.NAME)
                        .setPrefixEntityAttribute(entityService.getEntityAttribute(City.ENTITY_NAME, City.CITY_TYPE)
                                .withReference(CityType.class, CityType.SHORT_NAME))));

                if (!isRegionalLeader()) {
                    columns.add(new AbstractDomainColumn<>(new StringResourceModel("region", WorkerPage.this), new Sort("region")) {
                        @Override
                        public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                            Long regionId = domainService.getParentId(City.ENTITY_NAME, rowModel.getObject().getCityId());

                            Region region = domainService.getDomain(Region.class, regionId);

                            cellItem.add(new Label(componentId, Attributes.capitalizeWords(region.getTextValue(Region.NAME))));
                        }

                        @Override
                        public Component newFilter(String componentId, Table<Worker> table) {
                            return new TextFilter<>(componentId, PropertyModel.of(table.getFilterWrapper(), "map.region"));
                        }
                    });
                }

                if (isRegionalLeader()) {
                    columns.add(new DomainColumn<>(entity.getEntityAttribute(Worker.BIRTHDAY)));
                    columns.add(new DomainColumn<>(entity.getEntityAttribute(Worker.PHONE)));
                    columns.add(new DomainColumn<>(entity.getEntityAttribute(Worker.EMAIL)) {
                        @Override
                        public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                            cellItem.add(new Label(componentId, Objects.requireNonNullElse(rowModel.getObject().getEmail(), "").toLowerCase()));
                        }
                    });
                    columns.add(new DomainColumn<>(entity.getEntityAttribute(Worker.REGISTRATION_DATE)));
                }

                columns.add(new AbstractDomainColumn<>(new StringResourceModel("subWorkersCount", WorkerPage.this),
                        new Sort("subWorkersCount")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                        cellItem.add(new Label(componentId, rowModel.getObject().getSubWorkerCount()));
                    }

                    @Override
                    public Component newFilter(String componentId, Table<Worker> table) {
                        return new TextFilter<>(componentId, new Model<>());
                    }
                });

                if (isRegionalLeader()) {
                    columns.add(new AbstractDomainColumn<>(new StringResourceModel("mkStatus", WorkerPage.this), new Sort("mkStatus")) {
                        @Override
                        public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                            String mkStatus = domainService.getTextValue(MkStatus.ENTITY_NAME, rowModel.getObject().getMkStatus(), MkStatus.NAME);

                            cellItem.add(new Label(componentId, mkStatus));
                        }

                        @Override
                        public Component newFilter(String componentId, Table<Worker> table) {
                            return new TextFilter<>(componentId, PropertyModel.of(table.getFilterWrapper(), "map.mkStatus"));
                        }
                    });

                    columns.add(new AbstractDomainColumn<>(new StringResourceModel("manager", WorkerPage.this), new Sort("manager")) {
                        @Override
                        public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                            cellItem.add(new Label(componentId, workerService.getFio(rowModel.getObject().getManagerId())));
                        }

                        @Override
                        public Component newFilter(String componentId, Table<Worker> table) {
                            return new TextFilter<>(componentId, PropertyModel.of(table.getFilterWrapper(), "map.manager"));
                        }
                    });
                }

                if (!isRegionalLeader()) {
                    columns.add(new AbstractDomainColumn<>(new StringResourceModel("level", WorkerPage.this), new Sort("level")) {
                        @Override
                        public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                            cellItem.add(new Label(componentId, rowModel.getObject().getLevel() - getCurrentWorker().getLevel()));
                        }

                        @Override
                        public Component newFilter(String componentId, Table<Worker> table) {
                            return new TextFilter<>(componentId, new Model<>());
                        }
                    });
                }

                columns.add(new DomainActionColumn<>(WorkerPage.class){
                    @Override
                    protected void onAction(AjaxRequestTarget target, Table<Worker> table) {
                        target.add(structure);
                    }

                    @Override
                    public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                        PageParameters pageParameters = new PageParameters();
                        pageParameters.add("id", rowModel.getObject().getObjectId());

                        RepeatingView repeatingView = new RepeatingView(componentId);
                        cellItem.add(repeatingView);

                        repeatingView.add(new LinkPanel(repeatingView.newChildId(), new BootstrapBookmarkablePageLink<>(LinkPanel.COMPONENT_ID,
                                WorkerPage.this.getClass(), pageParameters, Buttons.Type.Link).setIconType(GlyphIconType.edit)));

                        if (!isViewOnly()) {
                            repeatingView.add(new LinkPanel(repeatingView.newChildId(), new BootstrapAjaxLink<Worker>(LinkPanel.COMPONENT_ID,
                                    Buttons.Type.Link) {
                                @Override
                                public void onClick(AjaxRequestTarget target) {
                                    workerRemoveModal.delete(rowModel.getObject().getObjectId(), target);
                                }

                                @Override
                                protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                                    super.updateAjaxAttributes(attributes);

                                    attributes.setEventPropagation(AjaxRequestAttributes.EventPropagation.STOP);
                                }

                                @Override
                                public boolean isVisible() {
                                    return  !rowModel.getObject().getStatus().equals(Status.ARCHIVE) &&
                                            !Objects.equals(rowModel.getObject().getObjectId(), 1L) &&
                                            !Objects.equals(rowModel.getObject().getObjectId(), getCurrentWorker().getObjectId());
                                }
                            }.setIconType(GlyphIconType.remove)));
                        }
                    }

                    @Override
                    public String getCssClass() {
                        return "domain-id-column" + (!isViewOnly() ? " domain-action" : "");
                    }
                });

                Table<Worker> table = new Table<>("table", columns, provider, 15, "workerPage"){
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

                        if (rowItem.getModelObject().getStatus().equals(Status.ARCHIVE)){
                            rowItem.add(new CssClassNameAppender("active"));
                        }else if (rowItem.getModelObject().getWorkerStatus() != null &&
                                rowItem.getModelObject().getWorkerStatus() == WorkerStatus.MANAGER_CHANGED){
                            rowItem.add(new CssClassNameAppender("info"));
                        }

                        return rowItem;

                    }
                };
                structure.add(table);

                WebMarkupContainer structureDepth = new WebMarkupContainer("structureDepth");
                structureDepth.setVisible(!isRegionalLeader());
                structure.add(structureDepth);

                long workerLevelDepth = worker.getObjectId() != null ? workerMapper.getWorkerLevelDepth(worker.getObjectId()) : 1;

                IModel<Long> graphLevelDepthModel = Model.of(workerLevelDepth);

                DropDownChoice<Long> levelDepth = new DropDownChoice<>("levelDepth", graphLevelDepthModel,
                        LongStream.rangeClosed(1, workerLevelDepth).boxed().sorted(Collections.reverseOrder())
                                .collect(Collectors.toList()));
                levelDepth.setNullValid(false);
                levelDepth.add(OnChangeAjaxBehavior.onChange(target -> {}));
                structureDepth.add(levelDepth );

                structureDepth.add(new Link<Void>("structureLink") {
                    @Override
                    public void onClick() {
                        setResponsePage(WorkerStructurePage.class, new PageParameters().add("id", worker.getObjectId())
                                .add("level", graphLevelDepthModel.getObject()));
                    }
                });

                structure.add(new AjaxButton("create") {
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

                WorkerInviteModal workerInviteModal = new WorkerInviteModal("inviteModal");
                structure.add(workerInviteModal);

                structure.add(new AjaxLink<Worker>("invite") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        workerInviteModal.invite(worker.getObjectId(), target);
                    }

                    @Override
                    public boolean isVisible() {
                        return worker.getObjectId() != null;
                    }
                });

                return structure;
            }
        });

        if (isFinance()) {
            tabs.add(new AbstractTab(new StringResourceModel("sales", WorkerPage.this)) {
                @Override
                public WebMarkupContainer getPanel(String panelId) {
                    Fragment sale = new Fragment(panelId, "sale", WorkerPage.this);

                    sale.add(new SalePanel("sale", worker));

                    return sale;
                }
            });
        }

        if (isFinance()) {
            tabs.add(new AbstractTab(new StringResourceModel("payments", WorkerPage.this)) {
                @Override
                public WebMarkupContainer getPanel(String panelId) {
                    Fragment payment = new Fragment(panelId, "payment", WorkerPage.this);

                    payment.add(new PaymentPanel("payment", worker));

                    return payment;
                }
            });
        }

        if (isFinance()) {
            tabs.add(new AbstractTab(new StringResourceModel("finance", WorkerPage.this)) {
                @Override
                public WebMarkupContainer getPanel(String panelId) {
                    Fragment finance = new Fragment(panelId, "finance", WorkerPage.this){
                        @Override
                        public boolean isVisible() {
                            return worker.getObjectId() != null;
                        }
                    };
                    finance.setOutputMarkupId(true);
                    finance.setOutputMarkupPlaceholderTag(true);

                    if (worker.getObjectId() != null) {
                        IModel<Period> periodModel = Model.of(periodMapper.getActualPeriod());

                        finance.add(new Label("month", LoadableDetachableModel.of(() -> Dates.getDateText(periodModel.getObject().getOperatingMonth()))));

                        IModel<BigDecimal> inputModel = LoadableDetachableModel.of(() ->
                                accountService.getBalance(worker.getObjectId(), periodModel.getObject().getObjectId()));

                        IModel<BigDecimal> chargedModel = LoadableDetachableModel.of(() ->
                                rewardService.getRewardsLocal(periodModel.getObject().getObjectId(), worker.getObjectId(), RewardStatus.CHARGED));
                        IModel<BigDecimal> withdrawModel = LoadableDetachableModel.of(() ->
                                rewardService.getRewardsLocal(periodModel.getObject().getObjectId(), worker.getObjectId(), RewardStatus.WITHDRAWN));
                        IModel<BigDecimal> paidModel = LoadableDetachableModel.of(() ->
                                rewardService.getRewardsLocal(periodModel.getObject().getObjectId(), worker.getObjectId(), RewardStatus.PAID));
                        IModel<BigDecimal> spentModel = LoadableDetachableModel.of(() ->
                                rewardService.getRewardsLocal(periodModel.getObject().getObjectId(), worker.getObjectId(), RewardStatus.SPENT));

                        finance.add(new Label("input", inputModel));
                        finance.add(new Label("charged", chargedModel));
                        finance.add(new Label("withdraw", withdrawModel));
                        finance.add(new Label("paid", paidModel));
                        finance.add(new Label("spent", spentModel));

                        finance.add(new Label("balance", LoadableDetachableModel.of(() -> {
                            Long currencyId = workerService.getCurrencyId(worker.getObjectId());

                            String symbol = domainService.getText(Currency.ENTITY_NAME, currencyId, Currency.SYMBOL);

                            return inputModel.getObject().add(chargedModel.getObject())
                                    .subtract(withdrawModel.getObject())
                                    .subtract(paidModel.getObject())
                                    .subtract(spentModel.getObject()) + symbol;
                        })));

                        IModel<WorkerReward> rewardModel = LoadableDetachableModel.of(() -> {
                                WorkerReward workerReward = rewardService.getWorkerRewardTreeCache(periodModel.getObject().getObjectId())
                                        .getWorkerReward(worker.getObjectId());

                                return workerReward != null ? workerReward : new WorkerReward(new WorkerNode());
                        });

                        finance.add(new Label("sale_volume", LoadableDetachableModel.of(() ->
                                rewardModel.getObject().getSaleVolume())));
                        finance.add(new Label("payment_volume", LoadableDetachableModel.of(() ->
                                rewardModel.getObject().getPaymentVolume())));
                        finance.add(new Label("registration_count", LoadableDetachableModel.of(() ->
                                rewardModel.getObject().getRegistrationCount())));

                        finance.add(new Label("reward_pv", LoadableDetachableModel.of(() ->
                                getRewardsTotalString(RewardType.PERSONAL_VOLUME, periodModel.getObject().getObjectId()))));
                        finance.add(new Label("reward_mk", LoadableDetachableModel.of(() ->
                                getRewardsTotalString(RewardType.PERSONAL_MYCOOK, periodModel.getObject().getObjectId()))));
                        finance.add(new Label("reward_ba", LoadableDetachableModel.of(() ->
                                getRewardsTotalString(RewardType.PERSONAL_RANGE, periodModel.getObject().getObjectId()))));
                        finance.add(new Label("reward_mkb", LoadableDetachableModel.of(() ->
                                getRewardsTotalString(RewardType.MANAGER_BONUS, periodModel.getObject().getObjectId()))));
                        finance.add(new Label("reward_cw", LoadableDetachableModel.of(() ->
                                getRewardsTotalString(RewardType.CULINARY_WORKSHOP, periodModel.getObject().getObjectId()))));

                        finance.add(new Link<Void>("rankLink") {
                            @Override
                            public void onClick() {
                                setResponsePage(WorkerStructurePage.class, new PageParameters().add("id", worker.getObjectId()).add("level", 0));
                            }
                        }
                        .add(new Label("rank", LoadableDetachableModel.of(() ->
                                rewardModel.getObject().getRank() !=  null && rewardModel.getObject().getRank() > 0
                                        ? domainService.getDomain(Rank.class, rewardModel.getObject().getRank()).getName()
                                        : ""))));

                        finance.add(new Label("group_sale_volume", LoadableDetachableModel.of(() ->
                                (rewardModel.getObject().getGroupSaleVolume()))));
                        finance.add(new Label("group_payment_volume", LoadableDetachableModel.of(() ->
                                rewardModel.getObject().getGroupPaymentVolume())));
                        finance.add(new Label("structure_sale_volume", LoadableDetachableModel.of(() ->
                                rewardModel.getObject().getStructureSaleVolume())));
                        finance.add(new Label("structure_payment_volume", LoadableDetachableModel.of(() ->
                                rewardModel.getObject().getStructurePaymentVolume())));
                        finance.add(new Label("group_registration_count", LoadableDetachableModel.of(() ->
                                rewardModel.getObject().getGroupRegistrationCount())));
                        finance.add(new Label("structure_manager_count", LoadableDetachableModel.of(() ->
                                rewardModel.getObject().getStructureManagerCount())));
                        finance.add(new Label("reward_mp", LoadableDetachableModel.of(() ->
                                getRewardsTotalString(RewardType.MANAGER_PREMIUM, periodModel.getObject().getObjectId()))));
                        finance.add(new Label("reward_gv", LoadableDetachableModel.of(() ->
                                getRewardsTotalString(RewardType.GROUP_VOLUME, periodModel.getObject().getObjectId()))));
                        finance.add(new Label("reward_sv", LoadableDetachableModel.of(() ->
                                getRewardsTotalString(RewardType.STRUCTURE_VOLUME, periodModel.getObject().getObjectId()))));

                        finance.add(new PeriodPanel("period"){
                            @Override
                            protected void onChange(AjaxRequestTarget target, Period period) {
                                periodModel.setObject(period);

                                rewardModel.detach();

                                target.add(finance);
                            }
                        });
                    }

                    return finance;
                }
            });
        }

        if (isFinance()) {
            tabs.add(new AbstractTab(new StringResourceModel("rewards", WorkerPage.this)) {
                @Override
                public WebMarkupContainer getPanel(String panelId) {
                    Fragment reward = new Fragment(panelId, "reward", WorkerPage.this);

                    reward.add(new RewardPanel("reward", worker));

                    return reward;
                }
            });
        }

        if (isAdmin()) {
            tabs.add(new AbstractTab(new StringResourceModel("history", WorkerPage.this)) {
                @Override
                public WebMarkupContainer getPanel(String panelId) {
                    Fragment history = new Fragment(panelId, "history", WorkerPage.this);

                    Entity workerEntity = entityMapper.getEntity(Worker.ENTITY_NAME);

                    List<IColumn<Attribute, String>> historyColumns = new ArrayList<>();

                    historyColumns.add(new AbstractColumn<>(new StringResourceModel("date", WorkerPage.this), "date") {
                        @Override
                        public void populateItem(Item<ICellPopulator<Attribute>> cellItem, String componentId, IModel<Attribute> rowModel) {
                            cellItem.add(new DateTimeLabel(componentId, rowModel.getObject().getStartDate()));
                        }
                    });

                    historyColumns.add(new AbstractColumn<>(new StringResourceModel("name", WorkerPage.this), "name") {
                        @Override
                        public void populateItem(Item<ICellPopulator<Attribute>> cellItem, String componentId, IModel<Attribute> rowModel) {
                            if (rowModel.getObject().getEntityAttributeId() == 1000){
                                cellItem.add(new Label(componentId, getString("role")));
                            }else{
                                EntityAttribute entityAttribute = workerEntity.getEntityAttribute(rowModel.getObject().getEntityAttributeId());

                                cellItem.add(new Label(componentId, entityAttribute.getValue() != null
                                        ? entityAttribute.getValue().getText()
                                        : entityAttribute.getEntityAttributeId().toString()));
                            }
                        }
                    });

                    historyColumns.add(new AbstractColumn<>(new StringResourceModel("value", WorkerPage.this), "value") {
                        @Override
                        public void populateItem(Item<ICellPopulator<Attribute>> cellItem, String componentId, IModel<Attribute> rowModel) {
                            Attribute attribute = rowModel.getObject();

                            if (attribute.getEntityAttributeId() == 1000){
                                cellItem.add(new Label(componentId, attribute.getText()));

                                return;
                            }

                            EntityAttribute entityAttribute = workerEntity.getEntityAttribute(attribute.getEntityAttributeId());

                            String value = "";

                            switch (entityAttribute.getValueType()){
                                case TEXT_LIST:
                                    value = attribute.getValues().stream()
                                            .map(Value::getText)
                                            .collect(Collectors.joining(","));

                                    break;
                                case NUMBER_LIST:
                                case ENTITY_LIST:
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
                                    value = String.valueOf(attribute.getNumber());

                                    break;
                                case DATE:
                                    value = String.valueOf(attribute.getDate());

                                    break;
                                case ENTITY:
                                    Long objectId = attribute.getNumber();

                                    if (objectId != null) {
                                        if (entityAttribute.getEntityAttributeId().equals(Worker.LAST_NAME)) {
                                            value = nameService.getLastName(objectId);
                                        } else if (entityAttribute.getEntityAttributeId().equals(Worker.FIRST_NAME)) {
                                            value = nameService.getFirstName(objectId);
                                        } else if (entityAttribute.getEntityAttributeId().equals(Worker.MIDDLE_NAME)) {
                                            value = nameService.getMiddleName(objectId);
                                        } else if (entityAttribute.getEntityAttributeId().equals(Worker.CITY)) {
                                            value = domainService.getTextValue(City.ENTITY_NAME, objectId, City.NAME);
                                        } else if (entityAttribute.getEntityAttributeId().equals(Worker.MK_STATUS)) {
                                            value = domainService.getTextValue(MkStatus.ENTITY_NAME, objectId, MkStatus.NAME);
                                        } else if (entityAttribute.getEntityAttributeId().equals(Worker.MANAGER)) {
                                            value = workerService.getWorkerLabel(objectId);
                                        } else if (entityAttribute.getEntityAttributeId().equals(Worker.TYPE)) {
                                            value = objectId.equals(WorkerType.PK) ? getString("component." + WorkerType.PK)
                                                    : objectId.equals(WorkerType.USER) ? getString("component." + WorkerType.USER)
                                                    : objectId + "";
                                        }
                                    }

                                    break;
                            }

                            cellItem.add(new Label(componentId, value));
                        }
                    });

                    historyColumns.add(new AbstractColumn<>(new StringResourceModel("user", WorkerPage.this), "user") {
                        @Override
                        public void populateItem(Item<ICellPopulator<Attribute>> cellItem, String componentId, IModel<Attribute> rowModel) {
                            Long userId = rowModel.getObject().getUserId();

                            if (rowModel.getObject().getEntityAttributeId() == 1000) {
                                cellItem.add(new Label(componentId, workerService.getSimpleWorkerLabel(userId)));
                            }else{
                                Worker w = new Worker();

                                if (userId != null) {
                                    w = workerMapper.getWorkerByUserId(userId);
                                }

                                cellItem.add(new Label(componentId, w != null ? workerService.getSimpleWorkerLabel(w.getObjectId()) : userId + ""));
                            }
                        }
                    });

                    SortableDataProvider<Attribute, String> historyDataProvider = new SortableDataProvider<>() {
                        @Override
                        public Iterator<? extends Attribute> iterator(long first, long count) {
                            return workerMapper.getWorkerUserHistories(new FilterWrapper<>(first, count)
                                    .put("objectId", worker.getObjectId())
                                    .put("userId", worker.getParentId())).iterator();
                        }

                        @Override
                        public long size() {
                            return workerMapper.getWorkerUserHistoriesCount(new FilterWrapper<>()
                                    .put("objectId", worker.getObjectId())
                                    .put("userId", worker.getParentId()));
                        }

                        @Override
                        public IModel<Attribute> model(Attribute object) {
                            return Model.of(object);
                        }
                    };

                    DataTable<Attribute, String> historyDataTable = new DataTable<>("history", historyColumns,
                            historyDataProvider, 10){
                        @Override
                        public boolean isVisible() {
                            return worker.getObjectId() != null;
                        }
                    };
                    historyDataTable.addTopToolbar(new AjaxFallbackHeadersToolbar<>(historyDataTable, historyDataProvider));
                    historyDataTable.addBottomToolbar(new NavigationToolbar(historyDataTable, "workerPage_history"));
                    historyDataTable.setOutputMarkupId(true);

                    history.add(historyDataTable);

                    return history;
                }
            });
        }

        form.add(new AjaxBootstrapTabbedPanel<>("info", tabs).setVisible(worker.getObjectId() != null));

        Link<Worker> back = new Link<Worker>("back") {
            @Override
            public void onClick() {
                PageParameters pageParameters = new PageParameters();

                if (manager != null && manager.getObjectId() != null && !isCurrentWorkerPage()){
                    pageParameters.add("id", manager.getObjectId());
                }

                setResponsePage(WorkerPage.this.getClass(), pageParameters);
            }

            @Override
            public boolean isVisible() {
                return !isViewOnly();
            }
        };
        back.add(new Label("label", getString("cancel")));

        form.add(back);
    }

    protected String getRewardsTotalString(Long rewardTypeId, Long periodId) {
        BigDecimal estimated = rewardService.getRewardsPointSumByWorker(periodId, worker.getObjectId(), rewardTypeId, RewardStatus.ESTIMATED);
        BigDecimal charged = rewardService.getRewardsPointSumByWorker(periodId, worker.getObjectId(), rewardTypeId, RewardStatus.CHARGED);

        return charged.toPlainString() + (estimated.compareTo(BigDecimal.ZERO) > 0 ?  " (" + estimated.toPlainString() + ")" : "");
    }

    protected FilterWrapper<Worker> newFilterWrapper() {
        return FilterWrapper.of(new Worker(worker.getLeft(), worker.getRight(), worker.getLevel()))
                .setStatus(FilterWrapper.STATUS_ACTIVE_AND_ARCHIVE)
                .sort("level", true);
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

    protected boolean isFinance() {
        return true;
    }
}
