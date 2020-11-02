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

    private Setting photoDirSetting;
    private Setting inviteSecretSetting;

    public SettingPage() {
        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        Form form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);

        photoDirSetting = domainService.getDomain(Setting.class, Setting.PHOTO_ID);

        if (photoDirSetting == null){
            photoDirSetting = new Setting();
        }

        TextField<String> photoDir = new TextField<>("photoDir", new TextAttributeModel(photoDirSetting, Setting.VALUE));
        form.add(photoDir);


        inviteSecretSetting = domainService.getDomain(Setting.class, Setting.INVITE_SECRET);

        if (inviteSecretSetting == null){
            inviteSecretSetting = new Setting();
        }

        TextField<String> inviteKey = new TextField<>("inviteSecret", new TextAttributeModel(inviteSecretSetting, Setting.VALUE));
        form.add(inviteKey);

        form.add(new AjaxButton("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                if (photoDirSetting.getObjectId() == null){
                    photoDirSetting.setObjectId(Setting.PHOTO_ID);

                    domainService.insert(photoDirSetting);
                }else{
                    domainService.update(photoDirSetting);
                }

                if (inviteSecretSetting.getObjectId() == null){
                    inviteSecretSetting.setObjectId(Setting.INVITE_SECRET);

                    domainService.insert(inviteSecretSetting);
                }else{
                    domainService.update(inviteSecretSetting);
                }

                info(getString("info_saved"));

                target.add(feedback);
            }
        });
    }
}
