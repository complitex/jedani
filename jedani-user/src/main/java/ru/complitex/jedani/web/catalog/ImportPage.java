package ru.complitex.jedani.web.catalog;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.jedani.service.ImportService;
import ru.complitex.jedani.web.BasePage;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 23.11.2017 16:52
 */
public class ImportPage extends BasePage{
    private Logger log = LoggerFactory.getLogger(ImportPage.class);

    @Inject
    private ImportService importService;

    public ImportPage() {
        FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        Form regionForm = new Form("regionForm");
        regionForm.setOutputMarkupId(true);
        regionForm.setMultiPart(true);
        add(regionForm);

        FileUploadField uploadField = new FileUploadField("uploadField");
        regionForm.add(uploadField);

        regionForm.add(new AjaxSubmitLink("upload") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    ImportService.Status status = importService.importRegions(uploadField.getFileUpload().getInputStream());

                    if (status.getErrorMessage() == null){
                        info("Успешно импортированно " + status.getCount() + " районов");
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
    }
}
