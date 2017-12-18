package ru.complitex.jedani.user.web.catalog;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketPushBroadcaster;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
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

    private class PushMessage implements IWebSocketPushMessage{
        private String text;

        public PushMessage(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

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
                        info("Добавлено " + status.getCount() + " районов");
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
                        info("Добавлено " + status.getCount() + " населенных пунктов");
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

        Label info  = new Label("info", Model.of(""));
        info.setOutputMarkupId(true);
        info.add(new WebSocketBehavior(){
            @Override
            protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
                if (message instanceof PushMessage){
                    String s = ((PushMessage)message).getText();

                    if (!info.getDefaultModelObject().equals(s)){
                        info.setDefaultModelObject(s);
                        handler.add(info);
                    }
                }
            }
        });
        userForm.add(info);

        userForm.add(new IndicatingAjaxButton("upload") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    WebSocketPushBroadcaster broadcaster = new WebSocketPushBroadcaster(WebSocketSettings.Holder.get(getApplication()).getConnectionRegistry());

                    ImportService.Status status =
                            importService.importUsers(cityUploadField.getFileUpload().getInputStream(),
                                    p -> broadcaster.broadcastAll(getApplication(), new PushMessage((int)(p*100) + "%")));

                    info.setDefaultModelObject("");
                    target.add(info);
                    if (status.getErrorMessage() == null){
                        info("Добавлено " + status.getCount() + " пользователей");
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
