package ru.complitex.domain.component.form;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.util.Attributes;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 02.05.2018 7:56
 */
public class AttributeSelect extends DropDownChoice<Long> {
    @Inject
    private DomainService domainService;

    private final String entityName;
    private final Long entityAttributeId;

    public AttributeSelect(String id, IModel<Long> model, String entityName, Long entityAttributeId) {
        super(id);

        this.entityName = entityName;
        this.entityAttributeId = entityAttributeId;

        setModel(model);

        setChoices(new LoadableDetachableModel<>() {
            @Override
            protected List<Long> load() {
                return domainService.getDomainIds(getFilterWrapper());
            }
        });

        setChoiceRenderer(new IChoiceRenderer<>() {
            @Override
            public Object getDisplayValue(Long id) {
                return AttributeSelect.this.getDisplayValue(id);
            }

            @Override
            public String getIdValue(Long object, int index) {
                return object.toString();
            }

            @Override
            public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                return id != null && !id.isEmpty() ? Long.valueOf(id) : null;
            }
        });

        setNullValid(true);
    }

    protected String getDisplayValue(Long id) {
        return Attributes.capitalize(domainService.getTextValue(entityName, id, entityAttributeId));
    }

    protected FilterWrapper<? extends Domain> getFilterWrapper(){
        return FilterWrapper.of(new Domain(entityName));
    }

    @Override
    protected String getNullKeyDisplayValue() {
        return "";
    }
}
