package ru.complitex.jedani.worker.api;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.cdi.NonContextual;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import ru.complitex.common.util.Dates;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.entity.WorkerReward;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.jedani.worker.service.InviteService;
import ru.complitex.jedani.worker.service.RewardService;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.json.*;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * @author Anatoly Ivanov
 * 27.07.2020 16:23
 */
@AuthorizeInstantiation(JedaniRoles.AUTHORIZED)
public class WorkerResourceReference extends ResourceReference {
    public static final WorkerResourceReference INSTANCE = new WorkerResourceReference();

    public WorkerResourceReference() {
        super("WorkerResourceReference");
    }

    @Override
    public IResource getResource() {
        return new AbstractResource() {
            @Override
            protected ResourceResponse newResourceResponse(Attributes attributes) {
                ResourceResponse resourceResponse = new ResourceResponse();

                resourceResponse.setContentType("application/json");
                resourceResponse.setTextEncoding("utf-8");

                resourceResponse.setWriteCallback(new WriteCallback() {
                    @Override
                    public void writeData(Attributes attributes) {
                        Principal principal = ((HttpServletRequest)attributes.getRequest().getContainerRequest()).getUserPrincipal();

                        if (principal == null){
                            attributes.getResponse()
                                    .write(Json.createObjectBuilder()
                                            .add("error", -1)
                                            .add("error_message", "user is not logged in")
                                            .build()
                                            .toString());

                            return;
                        }

                        String login = principal.getName();

                        JsonObjectBuilder json = Json.createObjectBuilder();

                        WorkerService workerService = new WorkerService();
                        NonContextual.of(WorkerService.class).inject(workerService);

                        InviteService inviteService = new InviteService();
                        NonContextual.of(InviteService.class).inject(inviteService);

                        Worker worker = workerService.getWorker(login);

                        if (worker != null){
                            json.add("worker_id", worker.getObjectId());
                            json.add("last_name", workerService.getLastName(worker));
                            json.add("first_name", workerService.getFirstName(worker));
                            json.add("middle_name", workerService.getMiddleName(worker));
                            json.add("j_id", worker.getJId() != null ? worker.getJId() : "");

                            json.add("status", worker.getMkStatus() != null ? worker.getMkStatus() : 0);

                            json.add("login", login);

                            JsonArrayBuilder phones = Json.createArrayBuilder();
                            worker.getPhones().forEach(phones::add);
                            json.add("phones", phones.build());

                            json.add("email", worker.getEmail() != null ? worker.getEmail() : "");

                            RewardService rewardService = new RewardService();
                            NonContextual.of(RewardService.class).inject(rewardService);

                            WorkerReward workerReward = rewardService.getWorkerReward(worker);

                            json.add("sale_volume", workerReward.getSaleVolume());
                            json.add("payment_volume", workerReward.getPaymentVolume());
                            json.add("group_sale_volume", workerReward.getGroupSaleVolume());
                            json.add("group_payment_volume", workerReward.getGroupSaleVolume());

                            json.add("birthday", Dates.getDateText(worker.getBirthday()));
                            json.add("registration_date", Dates.getDateText(worker.getRegistrationDate()));

                            JsonArrayBuilder regions = Json.createArrayBuilder();
                            workerService.getRegions(worker).forEach(regions::add);
                            json.add("regions", regions.build());

                            JsonArrayBuilder cities = Json.createArrayBuilder();
                            workerService.getCities(worker).forEach(cities::add);
                            json.add("cities", cities.build());

                            json.add("position", worker.getPosition() != null ? worker.getPosition() : 0);

                            JsonArrayBuilder roles = Json.createArrayBuilder();
                            workerService.getUser(worker).getRoles().forEach(roles::add);
                            json.add("roles", roles.build());

                            json.add("invite_key", inviteService.encodeKey(worker.getJId()));
                        }else {
                            json.add("error", -2);
                            json.add("error_message", "worker not found");
                        }

                        attributes.getResponse().write(json.build().toString());
                    }
                });

                return resourceResponse;
            }
        };
    }
}
