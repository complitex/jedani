package ru.complitex.jedani.worker.api;

import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.MarkupType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.handler.TextRequestHandler;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.jedani.worker.service.WorkerService;
import ru.complitex.user.entity.User;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

/**
 * @author Anatoly Ivanov
 * 11.08.2020 16:32
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class WorkerListJsonPage extends WebPage {
    @Inject
    private DomainService domainService;

    @Inject
    private WorkerService workerService;


    public WorkerListJsonPage() {
        JsonArrayBuilder array = Json.createArrayBuilder();

        domainService.getDomains(Worker.class, FilterWrapper.of(new Worker()))
                .forEach(w -> {
                    try {
                        JsonObjectBuilder json = Json.createObjectBuilder();

                        json.add("worker_id", w.getObjectId());

                        json.add("last_name", workerService.getLastName(w));
                        json.add("first_name", workerService.getFirstName(w));
                        json.add("middle_name", workerService.getMiddleName(w));

                        User u = workerService.getUser(w);

                        json.add("login", u != null ? u.getLogin() : "");

                        JsonArrayBuilder phones = Json.createArrayBuilder();
                        w.getPhones().forEach(phones::add);
                        json.add("phones", phones.build());

                        json.add("email", w.getEmail() != null ? w.getEmail() : "");

                        JsonArrayBuilder regions = Json.createArrayBuilder();
                        workerService.getRegions(w).forEach(regions::add);
                        json.add("regions", regions.build());

                        JsonArrayBuilder cities = Json.createArrayBuilder();
                        workerService.getCities(w).forEach(cities::add);
                        json.add("cities", cities.build());

                        array.add(json);
                    } catch (Exception e) {
                        System.out.println(w);

                        e.printStackTrace();

                        throw e;
                    }
                });

        getRequestCycle().scheduleRequestHandlerAfterCurrent(
                new TextRequestHandler("application/json", "UTF-8", array.build().toString()));
    }

    @Override
    public MarkupType getMarkupType() {
        return new MarkupType("html","application/json");
    }
}
