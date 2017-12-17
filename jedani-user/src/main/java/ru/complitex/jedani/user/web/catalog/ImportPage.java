package ru.complitex.jedani.user.web.catalog;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.jedani.user.service.ImportService;
import ru.complitex.jedani.user.web.BasePage;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 23.11.2017 16:52
 */
public class ImportPage extends BasePage{
    private Logger log = LoggerFactory.getLogger(ImportPage.class);

    @Inject
    private transient ImportService importService;

    public ImportPage() {
        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        //Regions

        Form regionForm = new Form("regionForm");
        regionForm.setOutputMarkupId(true);
        regionForm.setMultiPart(true);
        add(regionForm);

        FileUploadField regionUploadField = new FileUploadField("uploadField");
        regionForm.add(regionUploadField);

        regionForm.add(new IndicatingAjaxButton("upload") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    ImportService.Status status = importService.importRegions(regionUploadField.getFileUpload().getInputStream());

                    if (status.getErrorMessage() == null){
                        info("Импортировано " + status.getCount() + " районов");
                    }else{
                        error("Ошибка импорта " + status.getErrorMessage());
                    }
                } catch (Exception e) {
                    log.error("error import regions", e);
                }finally {
                    target.add(feedback, regionForm);
                }
            }
        });

        //Cities

        Form cityForm = new Form("cityForm");
        cityForm.setOutputMarkupId(true);
        cityForm.setMultiPart(true);
        add(cityForm);

        FileUploadField cityUploadField = new FileUploadField("uploadField");
        cityForm.add(cityUploadField);

        cityForm.add(new IndicatingAjaxButton("upload") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    ImportService.Status status = importService.importCities(cityUploadField.getFileUpload().getInputStream());

                    if (status.getErrorMessage() == null){
                        info("Импортировано " + status.getCount() + " населенных пунктов");
                    }else{
                        error("Ошибка импорта " + status.getErrorMessage());
                    }
                } catch (Exception e) {
                    log.error("error import cities", e);
                }finally {
                    target.add(feedback, cityForm);
                }
            }
        });

        //Users

        Form userForm = new Form("userForm");
        userForm.setOutputMarkupId(true);
        userForm.setMultiPart(true);
        add(userForm);

        FileUploadField userUploadField = new FileUploadField("uploadField");
        userForm.add(userUploadField);

        userForm.add(new AjaxSubmitLink("upload") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    ImportService.Status status = importService.importUsers(cityUploadField.getFileUpload().getInputStream());

                    if (status.getErrorMessage() == null){
                        info("Импортировано " + status.getCount() + " пользователей");
                    }else{
                        error("Ошибка импорта " + status.getErrorMessage());
                    }
                } catch (Exception e) {
                    log.error("error import users", e);
                }finally {
                    target.add(feedback, userForm);
                }
            }
        });
    }
}
