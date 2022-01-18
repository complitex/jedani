package ru.complitex.jedani.worker.page.period;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.util.WebSockets;
import ru.complitex.domain.component.form.AbstractEditModal;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.entity.RewardType;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.service.CompensationService;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Anatoly A. Ivanov
 * 18.11.2019 2:06 AM
 */
public class PeriodCalculateModal extends AbstractEditModal<Period> {
    private final Logger log = LoggerFactory.getLogger(PeriodCalculateModal.class);

    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private CompensationService compensationService;

    @Inject
    private WorkerService workerService;

    @Inject
    private DomainService domainService;

    private Label header;
    private WebMarkupContainer footer;

    public PeriodCalculateModal(String markupId) {
        super(markupId);

        header(new LoadableDetachableModel<>() {
            @Override
            protected String load() {
                return getString("header") + " " + Dates.getMonthText(getModelObject().getOperatingMonth());
            }
        });

        setModel(Model.of(new Period()));

        Label info = new Label("info", Model.of(""));
        info.setOutputMarkupId(true);
        add(info);

        add(new WebSocketBehavior(){
            @Override
            protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
                try {
                    if (message instanceof WebSockets.TextMessage){
                        try {
                            String text = ((WebSockets.TextMessage)message).getText();

                            info.setDefaultModelObject(text);

                            handler.add(info);
                        } catch (Exception e) {
                            log.error("info error ", e);
                        }
                    }

                    if (message instanceof WebSockets.CommandMessage) {
                        WebSockets.CommandMessage commandMessage = (WebSockets.CommandMessage) message;

                        switch (commandMessage.getCommand()) {
                            case "begin":
                                info.setDefaultModelObject(getString("info_rewards_calculating"));

                                handler.add(info);

                                break;
                            case "done":
                                info.setDefaultModelObject(getString("info_rewards_calculated"));

                                handler.add(info);

                                break;
                            case "error":
                                info.setDefaultModelObject(getString("error_calculate_rewards"));

                                handler.add(info);

                                break;
                        }
                    }
                } catch (Exception e) {
                    log.error("error onPush", e);
                }
            }
        });
    }

    protected Component createHeaderLabel(String id, String label) {
        header = new Label(id, label);

        header.setOutputMarkupId(true);

        return header;
    }

    protected MarkupContainer createFooter(String id) {
        footer = new WebMarkupContainer(id);

        footer.setOutputMarkupId(true);

        return footer;
    }

    @Override
    public void create(AjaxRequestTarget target) {
        super.create(target);

        target.add(header);

        setModelObject(periodMapper.getActualPeriod());
    }

    private final AtomicBoolean calculating = new AtomicBoolean(false);

    @Override
    protected void save(AjaxRequestTarget target) {
        if (calculating.get()) {
            return;
        }

        calculating.set(true);

        ThreadContext threadContext = ThreadContext.get(true);

        int pageId = getPage().getPageId();

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                ThreadContext.restore(threadContext);

                AtomicLong time = new AtomicLong(System.currentTimeMillis());

                Thread.sleep(1000);

                WebSockets.sendMessage(new WebSockets.CommandMessage("begin"), pageId);

                compensationService.calculateRewards(reward -> {
                    log.info(reward.toString());

                    if (System.currentTimeMillis() - time.get() >= 250) {
                        Worker worker = workerService.getWorker(reward.getWorkerId());

                        String jId = workerService.getJId(reward.getWorkerId());

                        String lastName = workerService.getLastName(reward.getWorkerId());

                        String info = worker.getLevel() + ": " + jId + ", " + lastName + " - " +
                                domainService.getTextValue(RewardType.ENTITY_NAME, reward.getType(), RewardType.NAME);

                        WebSockets.sendMessage(new WebSockets.TextMessage(info), pageId);

                        time.set(System.currentTimeMillis());
                    }
                });

                Thread.sleep(1000);

                WebSockets.sendMessage(new WebSockets.CommandMessage("done"), pageId);
            } catch (Exception e) {
                log.error("error calculate rewards ", e);

                WebSockets.sendMessage(new WebSockets.CommandMessage("error"), pageId);
            } finally {
                ThreadContext.detach();

                calculating.set(false);
            }
        });
    }

    @Override
    protected void cancel(AjaxRequestTarget target) {
        if (calculating.get()) {
            return;
        }

        super.cancel(target);

        getOnUpdate().accept(target);
    }

    @Override
    protected ResourceModel getSaveLabelModel() {
        return new ResourceModel("calculate");
    }
}
