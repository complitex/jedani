package ru.complitex.jedani.worker.util;

import org.apache.wicket.util.lang.Objects;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.name.service.NameService;

/**
 * @author Anatoly A. Ivanov
 * 25.04.2018 21:47
 */
public class Workers {
    public static String getWorkerLabel(Long workerId, DomainService domainService, NameService nameService){
        if (workerId == null){
            return "";
        }

        return getWorkerLabel(domainService.getDomain(Worker.class, workerId), nameService);
    }

    public static String getWorkerLabel(Worker worker, NameService nameService){
        return Objects.defaultIfNull(worker.getText(Worker.J_ID), "") + " " +
                Attributes.capitalize(nameService.getLastName(worker.getNumber(Worker.LAST_NAME))) + " " +
                Attributes.capitalize(nameService.getFirstName(worker.getNumber(Worker.FIRST_NAME))) + " " +
                Attributes.capitalize(nameService.getMiddleName(worker.getNumber(Worker.MIDDLE_NAME)));
    }

    public static String getWorkerLabelSimple(Long workerId, DomainService domainService, NameService nameService){
        if (workerId == null){
            return "";
        }

        return getWorkerLabelSimple(domainService.getDomain(Worker.class, workerId), nameService);
    }

    public static String getWorkerLabelSimple(Worker worker, NameService nameService){
        return Objects.defaultIfNull(worker.getText(Worker.J_ID), "") + " " +
                Attributes.capitalize(nameService.getLastName(worker.getNumber(Worker.LAST_NAME)));
    }

}
