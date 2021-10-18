package ru.complitex.jedani.worker.api;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.MarkupType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.handler.TextRequestHandler;
import ru.complitex.common.util.Dates;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.entity.WorkerReward;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.jedani.worker.service.InviteService;
import ru.complitex.jedani.worker.service.RewardService;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

import static java.math.BigDecimal.ZERO;

/**
 * @author Anatoly Ivanov
 * 27.07.2020 16:23
 */
@AuthorizeInstantiation(JedaniRoles.AUTHORIZED)
public class WorkerJsonPage extends WebPage {
    @Inject
    private WorkerService workerService;

    @Inject
    private InviteService inviteService;

    @Inject
    private RewardService rewardService;

    public WorkerJsonPage() {
        Principal principal = ((HttpServletRequest)getRequest().getContainerRequest()).getUserPrincipal();

        if (principal == null) {
            getRequestCycle().scheduleRequestHandlerAfterCurrent(new TextRequestHandler("application/json", "UTF-8",
                    Json.createObjectBuilder()
                            .add("error", -1)
                            .add("error_message", "user is not logged in")
                            .build()
                            .toString()));
            return;
        }

        String login = principal.getName();

        JsonObjectBuilder json = Json.createObjectBuilder();

        Worker worker = workerService.getWorker(login);

        if (worker != null){
            json.add("worker_id", worker.getObjectId());
            json.add("last_name", workerService.getLastName(worker));
            json.add("first_name", workerService.getFirstName(worker));
            json.add("middle_name", workerService.getMiddleName(worker));

            if (worker.getJId() != null) {
                json.add("j_id", worker.getJId());
            }

            if (worker.getMkStatus() != null) {
                json.add("status", worker.getMkStatus());
            }

            json.add("login", login);

            JsonArrayBuilder phones = Json.createArrayBuilder();
            worker.getPhones().forEach(phones::add);
            json.add("phones", phones.build());

            if (worker.getEmail() != null) {
                json.add("email", worker.getEmail());
            }

            WorkerReward workerReward = rewardService.getWorkerReward(worker);

            json.add("sale_volume", workerReward != null ? workerReward.getSaleVolume(): ZERO);
            json.add("payment_volume", workerReward != null ? workerReward.getPaymentVolume() : ZERO);
            json.add("group_sale_volume", workerReward != null ? workerReward.getGroupSaleVolume() : ZERO);
            json.add("group_payment_volume", workerReward != null ? workerReward.getGroupPaymentVolume() : ZERO);
            json.add("structure_sale_volume", workerReward != null ? workerReward.getStructureSaleVolume() : ZERO);
            json.add("structure_payment_volume", workerReward != null ? workerReward.getStructurePaymentVolume() : ZERO);

            if (worker.getBirthday() != null) {
                json.add("birthday", Dates.getDateText(worker.getBirthday()));
            }

            json.add("registration_date", Dates.getDateText(worker.getRegistrationDate()));

            JsonArrayBuilder regions = Json.createArrayBuilder();
            workerService.getRegions(worker).forEach(regions::add);
            json.add("regions", regions.build());

            JsonArrayBuilder cities = Json.createArrayBuilder();
            workerService.getCities(worker).forEach(cities::add);
            json.add("cities", cities.build());

            if (worker.getPosition() != null) {
                json.add("position", worker.getPosition());
            }

            JsonArrayBuilder roles = Json.createArrayBuilder();
            workerService.getUser(worker).getRoles().forEach(roles::add);
            json.add("roles", roles.build());

            if (worker.getJId() != null) {
                json.add("invite_key", inviteService.encodeKey(worker.getJId()));
            }
        }else {
            json.add("error", -2);
            json.add("error_message", "worker not found");
        }

        getRequestCycle().scheduleRequestHandlerAfterCurrent(new TextRequestHandler("application/json", "UTF-8", json.build().toString()));
    }

    @Override
    public MarkupType getMarkupType() {
        return new MarkupType("html","application/json");
    }
}
