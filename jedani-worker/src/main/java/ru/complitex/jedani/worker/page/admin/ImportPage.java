package ru.complitex.jedani.worker.page.admin;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketPushBroadcaster;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.domain.service.DomainNodeService;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.service.ImportService;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 23.11.2017 16:52
 */
public class ImportPage extends BasePage{
    private Logger log = LoggerFactory.getLogger(ImportPage.class);

    @Inject
    private ImportService importService;

    @Inject
    private DomainNodeService domainNodeService;

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

        IModel<String> infoModel = Model.of("");
        Label info  = new Label("info", infoModel);
        info.setOutputMarkupId(true);
        info.add(new WebSocketBehavior(){
            @Override
            protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
                if (message instanceof PushMessage){
                    try {
                        String s = ((PushMessage)message).getText();

                        if (!infoModel.getObject().equals(s)){
                            infoModel.setObject(s);
                            handler.add(info);
                        }
                    } catch (Exception e) {
                        log.error("info error ", e);
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
                            importService.importWorkers(cityUploadField.getFileUpload().getInputStream(),
                                    p -> broadcaster.broadcastAll(getApplication(), new PushMessage(p)));

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

        add(new IndicatingAjaxLink<Void>("rebuildIndex") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    domainNodeService.rebuildRootIndex(Worker.ENTITY_NAME, 1L, Worker.MANAGER_ID);

                    info("Индекс обновлен успешно");
                } catch (Exception e) {
                    error("Ошибка обновления индекса " + e.getMessage());

                    log.error("Ошибка обновления индекса ", e);
                }

                target.add(feedback);
            }
        });
    }
}
