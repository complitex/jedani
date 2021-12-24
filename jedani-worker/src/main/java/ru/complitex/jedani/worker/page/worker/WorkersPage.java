package ru.complitex.jedani.worker.page.worker;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.jedani.worker.security.JedaniRoles;

/**
 * @author Ivanov Anatoliy
 */
@AuthorizeInstantiation({JedaniRoles.AUTHORIZED})
public class WorkersPage extends StructurePage {
    @Override
    protected boolean isStructure() {
        return false;
    }
}
