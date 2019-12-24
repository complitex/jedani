package ru.complitex.jedani.worker.page.admin;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import ru.complitex.domain.model.TextAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Setting;
import ru.complitex.jedani.worker.page.BasePage;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 10.01.2019 18:31
 */
public class SettingPage extends BasePage {
    @Inject
    private DomainService domainService;

    private Setting photoSetting;

    public SettingPage() {
        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        Form form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);

        photoSetting = domainService.getDomain(Setting.class, Setting.PHOTO_ID);

        if (photoSetting == null){
            photoSetting = new Setting();
        }

        TextField<String> photoDir = new TextField<>("photoDir", new TextAttributeModel(photoSetting, Setting.VALUE));
        form.add(photoDir);

        form.add(new AjaxButton("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                if (photoSetting.getObjectId() == null){
                    photoSetting.setObjectId(Setting.PHOTO_ID);

                    domainService.insert(photoSetting);
                }else{
                    domainService.update(photoSetting);
                }

                info(getString("info_saved"));

                target.add(feedback);
            }
        });
    }
}
