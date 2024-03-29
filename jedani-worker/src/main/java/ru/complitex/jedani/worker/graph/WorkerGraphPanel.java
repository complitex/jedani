package ru.complitex.jedani.worker.graph;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.template.PackageTextTemplate;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Status;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.graph.resource.CytoscapeCoseJsResourceReference;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
import ru.complitex.jedani.worker.service.cache.RewardTreeCacheService;
import ru.complitex.name.service.NameService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 13.06.2018 18:07
 */
public class WorkerGraphPanel extends Panel {
    @Inject
    private WorkerMapper workerMapper;

    @Inject
    private NameService nameService;

    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private RewardTreeCacheService rewardTreeCacheService;

    @Inject
    private DomainService domainService;

    private String elements;
    private String fileName;

    private final Worker worker;
    private final Long levelDepth;
    private final boolean volume;

    public WorkerGraphPanel(String id, Worker worker, Long levelDepth, boolean volume) {
        super(id);

        this.worker = worker;
        this.levelDepth = levelDepth;
        this.volume = volume;
    }

    private String getLabel(Worker worker){
        String lastName = nameService.getLastName(worker.getLastNameId());

        return lastName.substring(0, Math.min(6, lastName.length()));
    }

    private String getRewardLabel(RewardNode workerReward){
        String rewards = "";

        if (workerReward == null) {
            return "";
        }

        if (workerReward.getRank() > 0) {
            rewards += getString("rank") + ": " + domainService.getDomain(Rank.class, workerReward.getRank()).getName().toUpperCase() + "\\n";
        }

        if (volume) {
            if (workerReward.getSaleVolume().compareTo(BigDecimal.ZERO) > 0) {
                rewards += getString("sale_volume") + ": " + workerReward.getSaleVolume().toPlainString() + "\\n";
            }

            if (workerReward.getPaymentVolume().compareTo(BigDecimal.ZERO) > 0) {
                rewards += getString("payment_volume") + ": " + workerReward.getPaymentVolume().toPlainString() + "\\n";
            }

            if (workerReward.getGroupSaleVolume().compareTo(BigDecimal.ZERO) > 0) {
                rewards += getString("group_sale_volume") + ": " + workerReward.getGroupSaleVolume().toPlainString() + "\\n";
            }

            if (workerReward.getGroupPaymentVolume().compareTo(BigDecimal.ZERO) > 0) {
                rewards += getString("group_payment_volume") + ": " + workerReward.getGroupPaymentVolume().toPlainString() + "\\n";
            }

            if (workerReward.getStructureSaleVolume().compareTo(BigDecimal.ZERO) > 0) {
                rewards += getString("structure_sale_volume") + ": " + workerReward.getStructureSaleVolume().toPlainString() + "\\n";
            }

            if (workerReward.getStructurePaymentVolume().compareTo(BigDecimal.ZERO) > 0) {
                rewards += getString("structure_payment_volume") + ": " + workerReward.getStructurePaymentVolume().toPlainString() + "\\n";
            }

            if (!rewards.isEmpty()) {
                rewards += getString("first_level_count") + ": " + workerReward.getFirstLevelCount() + "\\n";
                rewards += getString("group_registration_count") + ": " + workerReward.getGroupRegistrationCount() + "\\n";
                rewards += getString("year_payment_volume") + ": " + workerReward.getYearPaymentVolume().toPlainString() + "\\n";
            }
        }

        return rewards;
    }

    private String getStyle(Worker w, int level) {
        String style = "";
        if (Objects.equals(w.getStatus(), Status.ARCHIVE)){
            style = "style: {'background-color': '#f5f5f5'}";
        }else if (Objects.equals(w.getWorkerStatus(), WorkerStatus.MANAGER_CHANGED)){
            style = "style: {'background-color': '#d9edf7'}";
        }

        return style;
    }


    @Override
    protected void onConfigure() {
        super.onConfigure();

        List<Worker> workers = new ArrayList<>(workerMapper.getWorkers(FilterWrapper.of(
                new Worker(worker.getLeft(), worker.getRight(), worker.getLevel()))
                .setStatus(FilterWrapper.STATUS_ACTIVE_AND_ARCHIVE)
                .put("levelDepth", levelDepth != 0 ? levelDepth : null)));

        RewardTree rewards = (levelDepth == 0)
                ? rewardTreeCacheService.getRewardTreeCache(periodMapper.getActualPeriod().getObjectId())
                : null;

        elements =  " {data: {id: '" + worker.getObjectId() + "', " +
                "label: '" + worker.getText(Worker.J_ID) + "\\n" + getLabel(worker) +
                (rewards != null ? "\\n" + getRewardLabel(rewards.getRewardNode(worker.getObjectId())) : "") + "'}, " +
                getStyle(worker, 0) + "}";

        if (!workers.isEmpty()) {
            elements += "," + workers.stream()
                    .filter(w -> rewards == null || !getRewardLabel(rewards.getRewardNode(w.getObjectId())).isEmpty())
                    .map(w -> " {data: {id: '" + w.getObjectId() + "', " +
                            "label: '" + w.getText(Worker.J_ID) + "\\n" + getLabel(w) +
                            (rewards != null ? "\\n" + getRewardLabel(rewards.getRewardNode(w.getObjectId())) : "") + "'}, " +
                            getStyle(w, worker.getLevel().intValue()) + "}")
                    .collect(Collectors.joining(","));

            elements += "," + workers.stream()
                    .filter(w -> rewards == null || !getRewardLabel(rewards.getRewardNode(w.getObjectId())).isEmpty())
                    .map(w -> " {data: {id: 'e" + w.getObjectId() + "', " +
                            "source: '" + w.getManagerId() + "', " +
                            "target: '" + w.getObjectId() + "'}}")
                    .collect(Collectors.joining(","));
        }

        fileName =  worker.getJId() + " " +
                nameService.getLastName(worker.getLastNameId()) + " " +
                nameService.getFirstName(worker.getFistNameId()) + " " +
                nameService.getMiddleName(worker.getMiddleNameId());

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forReference(CytoscapeCoseJsResourceReference.INSTANCE));

        response.render(OnDomReadyHeaderItem.forScript(new PackageTextTemplate(WorkerGraphPanel.class,
                levelDepth != 0 ? "resource/js/worker-graph.tmpl.js" : "resource/js/worker-graph-reward.tmpl.js")
                .asString(new HashMap<String, String>(){{
                    put("elements", elements);
                    put("fileName", fileName);
                }})));
    }
}
