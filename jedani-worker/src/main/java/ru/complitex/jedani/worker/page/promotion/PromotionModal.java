package ru.complitex.jedani.worker.page.promotion;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.address.entity.Country;
import ru.complitex.common.wicket.form.DateTextFieldFormGroup;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.common.wicket.form.TextFieldFormGroup;
import ru.complitex.common.wicket.util.ComponentUtil;
import ru.complitex.domain.component.form.AbstractDomainAutoCompleteList;
import ru.complitex.domain.component.form.DomainAutoCompleteFormGroup;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Status;
import ru.complitex.domain.model.*;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.domain.util.Locales;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Promotion;
import ru.complitex.jedani.worker.entity.Setting;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Anatoly A. Ivanov
 * 24.12.2018 20:01
 */
public class PromotionModal extends Modal<Promotion> {
    private Logger log = LoggerFactory.getLogger(PromotionModal.class);

    public final static String PROMOTION_FILE_PREFIX = "promotion_file_";

    @Inject
    private DomainService domainService;

    private String promotionDir;

    private WebMarkupContainer container;

    private NotificationPanel feedback;

    private FileUploadField file;

    private Component remove;

    public PromotionModal(String markupId) {
        super(markupId, new Model<>(new Promotion()));

        setBackdrop(Backdrop.FALSE);

        Setting promotionSetting = domainService.getDomain(Setting.class, Setting.PROMOTION);

        promotionDir = promotionSetting.getText(Setting.VALUE);

        header(new ResourceModel("header"));

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        container.setOutputMarkupPlaceholderTag(true);
        container.setVisible(false);
        add(container);

        feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        container.add(feedback);

        container.add(new DateTextFieldFormGroup("begin", new DateAttributeModel(getModel(), Promotion.BEGIN)));
        container.add(new DateTextFieldFormGroup("end", new DateAttributeModel(getModel(), Promotion.END)));
        container.add(new DomainAutoCompleteFormGroup("country", Country.ENTITY_NAME, Country.NAME,
                new NumberAttributeModel(getModel(), Promotion.COUNTRY)).setRequired(true));
        container.add(new TextFieldFormGroup<>("name", new TextValueModel(getModel(), Promotion.NAME,
                Locales.getSystemLocaleId())).setRequired(true));


        container.add(new DownloadLink("downloadFile", new LoadableDetachableModel<File>() {
            @Override
            protected File load() {
                Path filePath = new File(promotionDir, PROMOTION_FILE_PREFIX + getModel().getObject().getObjectId()).toPath();

                if (Files.exists(filePath)){
                    return filePath.toFile();
                }

                return null;
            }
        }, new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return getModel().getObject().getText(Promotion.FILE);
            }
        }).add(new Label("name", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return getModel().getObject().getText(Promotion.FILE);
            }
        })));

        file = new FileUploadField("file");
        container.add(file);

        container.add(new FormGroupPanel("nomenclatures", new AbstractDomainAutoCompleteList(FormGroupPanel.COMPONENT_ID,
                Nomenclature.ENTITY_NAME, new AttributeModel(getModel(), Promotion.NOMENCLATURES)) {
            @Override
            protected String getTextValue(Domain domain) {
                if (domain == null){
                    return "";
                }

                return Strings.defaultIfEmpty(domain.getText(Nomenclature.CODE), "") + " " +
                        Attributes.capitalize(domain.getTextValue(Nomenclature.NAME));
            }

            @Override
            protected Domain getFilterObject(String input) {
                Nomenclature nomenclature = new Nomenclature();

                nomenclature.getOrCreateAttribute(Nomenclature.CODE).setText(input);
                nomenclature.getOrCreateAttribute(Nomenclature.NAME).setText(input);

                return nomenclature;
            }
        }));

        container.add(new TextFieldFormGroup<>("eur", new TextAttributeModel(getModel(), Promotion.EUR, TextAttributeModel.TYPE.DEFAULT)));

        addButton(new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                PromotionModal.this.save(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                container.visitChildren(((object, visit) -> {
                    if (object.hasErrorMessage()){
                        target.add(ComponentUtil.getAjaxParent(object));
                    }
                }));
            }
        }.setLabel(new ResourceModel("save")));

        addButton(remove = new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                PromotionModal.this.remove(target);
            }

            @Override
            public boolean isVisible() {
                return PromotionModal.this.getModelObject().getObjectId() != null;
            }
        }.setLabel(new ResourceModel("remove")).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));


        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                PromotionModal.this.close(target);
            }
        }.setLabel(new ResourceModel("cancel")));
    }

    void create(AjaxRequestTarget target){
        setModelObject(new Promotion());

        open(target);
    }

    void edit(Promotion promotion, AjaxRequestTarget target){
        setModelObject(promotion);

        open(target);
    }

    private void open(AjaxRequestTarget target){
        container.setVisible(true);
        target.add(container, remove);
        appendShowDialogJavaScript(target);
    }

    private void close(AjaxRequestTarget target){
        appendCloseDialogJavaScript(target);

        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent)c).clearInput());
    }

    private void save(AjaxRequestTarget target){
        Promotion promotion = getModelObject();

        if (file.getFileUpload() != null){
            promotion.setText(Promotion.FILE, file.getFileUpload().getClientFileName());
        }

        domainService.save(promotion);

        if (file.getFileUpload() != null) {
            Path promotionPath = new File(promotionDir).toPath();

            if (!Files.exists(promotionPath) || !Files.isWritable(promotionPath)){
                error(getString("error_promotion_path") +  ": " + promotionPath.toString());

                target.add(feedback);

                return;
            }

            Path filePath = new File(promotionPath.toFile(), PROMOTION_FILE_PREFIX + promotion.getObjectId()).toPath();

            try {
                if (Files.exists(filePath)){
                    Files.move(filePath, new File(filePath.getParent().toFile(), PROMOTION_FILE_PREFIX +
                            promotion.getObjectId() + "_deleted_" + System.currentTimeMillis()).toPath());
                }

                Files.copy(file.getFileUpload().getInputStream(), filePath);
            } catch (Exception e) {
                log.error("promotion save error ", e);

                target.add(feedback);

                return;
            }
        }

        success(getString("info_promotion_saved"));

        close(target);
        onUpdate(target);
    }

    private void remove(AjaxRequestTarget target) {
        Promotion promotion = PromotionModal.this.getModelObject();

        promotion.setStatus(Status.ARCHIVE);

        domainService.save(promotion);

        success(getString("info_promotion_removed"));

        close(target);
        onUpdate(target);
    }

    protected void onUpdate(AjaxRequestTarget target){
    }
}
