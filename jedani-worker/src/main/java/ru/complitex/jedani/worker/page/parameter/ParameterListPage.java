package ru.complitex.jedani.worker.page.parameter;

import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Parameter;

import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
public class ParameterListPage extends DomainListModalPage<Parameter> {
    public ParameterListPage() {
        super(Parameter.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        return entity.getEntityAttributes(Parameter.PARAMETER_ID, Parameter.NAME);
    }

    @Override
    protected boolean isEditEnabled() {
        return false;
    }
}
