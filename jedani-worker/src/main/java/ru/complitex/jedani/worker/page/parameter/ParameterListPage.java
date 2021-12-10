package ru.complitex.jedani.worker.page.parameter;

import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Parameter;

/**
 * @author Ivanov Anatoliy
 */
public class ParameterListPage extends DomainListModalPage<Parameter> {
    public ParameterListPage() {
        super(Parameter.class);
    }

    @Override
    protected boolean isEditEnabled() {
        return false;
    }
}
