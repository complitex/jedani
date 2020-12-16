package ru.complitex.jedani.worker.page.invite;

import com.google.common.hash.Hashing;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.resource.FileSystemResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.util.Images;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.common.wicket.form.FormGroupTextField;
import ru.complitex.common.wicket.util.Wickets;
import ru.complitex.domain.component.form.FormGroupAttributeInputList;
import ru.complitex.domain.component.form.FormGroupDomainAutoComplete;
import ru.complitex.domain.entity.StringType;
import ru.complitex.domain.model.DateAttributeModel;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.model.TextAttributeModel;
import ru.complitex.domain.service.DomainNodeService;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.page.login.LoginPage;
import ru.complitex.jedani.worker.page.resource.JedaniCssResourceReference;
import ru.complitex.jedani.worker.page.resource.JedaniFaviconResourceReference;
import ru.complitex.jedani.worker.page.resource.JedaniLogoImgResourceReference;
import ru.complitex.jedani.worker.page.worker.WorkerPage;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.jedani.worker.service.CardService;
import ru.complitex.jedani.worker.service.InviteService;
import ru.complitex.jedani.worker.service.WorkerService;
import ru.complitex.name.entity.FirstName;
import ru.complitex.name.entity.LastName;
import ru.complitex.name.entity.MiddleName;
import ru.complitex.name.service.NameService;
import ru.complitex.user.entity.User;
import ru.complitex.user.mapper.UserMapper;

import javax.inject.Inject;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author Anatoly Ivanov
 * 01.11.2020 15:19
 */
public class InvitePage extends WebPage {
    private Logger log = LoggerFactory.getLogger(InvitePage.class);

    @Inject
    private UserMapper userMapper;

    @Inject
    private DomainService domainService;

    @Inject
    private WorkerService workerService;

    @Inject
    private CardService cardService;

    @Inject
    private NameService nameService;

    @Inject
    private DomainNodeService domainNodeService;

    @Inject
    private InviteService inviteService;

    private final Worker worker;

    private final String photoDir;
    private final FileUploadField photoUploadFiled;

    public InvitePage(PageParameters parameters) {
        setVersioned(false);

        String key = parameters.get("key").toString();

        String JId = inviteService.decodeJId(key);

        if (JId == null){
            throw new UnauthorizedInstantiationException(getClass());
        }

        Worker manager = workerService.getWorkerByJId(JId);

        worker = new Worker();

        worker.init();

        worker.setType(WorkerType.PK);
        worker.setText(Worker.J_ID, workerService.getNewJId());
        worker.setMkStatus(MkStatus.STATUS_JUST);
        worker.setText(Worker.INVITE, key);

        manager.getNumberValues(Worker.REGIONS).forEach(n -> worker.addNumberValue(Worker.REGIONS, n));
        manager.getNumberValues(Worker.CITIES).forEach(n -> worker.addNumberValue(Worker.CITIES, n));

        worker.setManagerId(manager.getObjectId());


        User user = new User(worker.getText(Worker.J_ID));

        user.addRole(JedaniRoles.USERS);


        add(new ResourceLink<>("favicon", JedaniFaviconResourceReference.INSTANCE));

        setVersioned(false);

        add(new Image("jedani", JedaniLogoImgResourceReference.INSTANCE));

        FeedbackPanel feedback = new NotificationPanel("feedback").showRenderedMessages(false);
        feedback.setOutputMarkupId(true);
        add(feedback);

        add(new Label("manager", workerService.getWorkerFio(manager)));

        Form<?> form = new Form<>("form");
        add(form);


        FormGroupDomainAutoComplete lastName, firstName, middleName;

        form.add(lastName = new FormGroupDomainAutoComplete("lastName", LastName.ENTITY_NAME, LastName.NAME,
                new NumberAttributeModel(worker, Worker.LAST_NAME)).setInputRequired(true));
        form.add(firstName = new FormGroupDomainAutoComplete("firstName", FirstName.ENTITY_NAME, FirstName.NAME,
                new NumberAttributeModel(worker, Worker.FIRST_NAME)).setInputRequired(true));
        form.add(middleName = new FormGroupDomainAutoComplete("middleName", MiddleName.ENTITY_NAME, MiddleName.NAME,
                new NumberAttributeModel(worker, Worker.MIDDLE_NAME)));

        form.add(new FormGroupDateTextField("birthday", new DateAttributeModel(worker, Worker.BIRTHDAY)));

        form.add(new FormGroupAttributeInputList("phone", Model.of(worker.getOrCreateAttribute(Worker.PHONE))).setRequired(true));
        form.add(new FormGroupTextField<>("email", new TextAttributeModel(worker, Worker.EMAIL, StringType.LOWER_CASE))
                .setRequired(true));


        Setting photoSetting = domainService.getDomain(Setting.class, Setting.PHOTO_ID);

        photoDir = photoSetting.getText(Setting.VALUE);

        form.add(new NonCachingImage("photo", new FileSystemResource(new File(photoDir,
                WorkerPage.PHOTO_FILE_PREFIX + worker.getObjectId()).toPath())){
            @Override
            public boolean isVisible() {
                return Files.exists(new File(photoDir, WorkerPage.PHOTO_FILE_PREFIX + worker.getObjectId()).toPath());
            }
        });

        photoUploadFiled = new FileUploadField("photoUploadFiled");
        form.add(photoUploadFiled);


        TextField<String> login = new TextField<>("userLogin", new PropertyModel<>(user, "login"));
        login.setRequired(true);
        form.add(login);

        PasswordTextField password = new PasswordTextField("userPassword", new PropertyModel<>(user, "password"));
        password.setRequired(true);
        form.add(password);

        PasswordTextField confirmPassword = new PasswordTextField("confirmPassword", new PropertyModel<>(user, "confirmPassword"));
        confirmPassword.setRequired(true);
        form.add(confirmPassword);

        FormGroupTextField<String> card = new FormGroupTextField<String>("card", new Model<>()){
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


        form.add(new IndicatingAjaxButton("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    if (worker.getObjectId() != null){
                        setResponsePage(LoginPage.class);
                    }

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

                    if (!user.getPassword().equals(user.getConfirmPassword())){
                        password.error(getString("error_confirm_password"));
                        target.add(feedback);
                        return;
                    }

                    user.setPassword(Hashing.sha256().hashString(user.getPassword(), StandardCharsets.UTF_8).toString());


                    if (userMapper.getUser(user.getLogin()) != null){
                        login.error(getString("error_login_exist"));
                        target.add(feedback);
                        return;
                    }

                    workerService.save(user, null);

                    worker.setParentId(user.getId());
                    worker.setParentEntityId(User.ENTITY_ID);
                    worker.setUserId(null);

                    worker.setLastNameId(nameService.getOrCreateLastName(lastName.getInput(), worker.getLastNameId()));
                    worker.setFirstNameId(nameService.getOrCreateFirstName(firstName.getInput(), worker.getFistNameId()));
                    worker.setMiddleNameId(nameService.getOrCreateMiddleName(middleName.getInput(), worker.getMiddleNameId()));

                    lastName.detachModels();
                    firstName.detachModels();
                    middleName.detachModels();

                    try {
                        workerService.save(worker);

                        if (cardNumber != null){
                            cardObject.setWorkerId(worker.getObjectId());

                            cardService.save(cardObject);
                        }
                    } catch (Exception e) {
                        userMapper.deleteUser(user);

                        throw e;
                    }

                    domainNodeService.updateIndex(manager, worker);

                    getSession().success(getString("info_worker_created"));

                    setResponsePage(LoginPage.class);

                    //photo

                    FileUpload photoFileUpload = photoUploadFiled.getFileUpload();

                    if (photoFileUpload != null) {
                        if (photoFileUpload.getSize() <= 314572800){
                            try {
                                File photoFileDir = new File(photoDir);

                                File photoFile = new File(photoFileDir, WorkerPage.PHOTO_FILE_PREFIX + worker.getObjectId());

                                if (Files.exists(photoFile.toPath())){
                                    Files.move(photoFile.toPath(), new File(photoFile.getParentFile(), WorkerPage.PHOTO_FILE_PREFIX +
                                            worker.getObjectId() + "_deleted_" +
                                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))).toPath());
                                }

                                Images.write(photoUploadFiled.getFileUpload().getInputStream(), 1920, 1080,
                                        "jpeg", photoFile);
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
                        userMapper.deleteUser(user);
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
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(JedaniCssResourceReference.INSTANCE));
    }
}
