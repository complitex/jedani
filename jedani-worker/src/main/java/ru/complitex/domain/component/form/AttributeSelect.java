package ru.complitex.domain.component.form;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.mapper.DomainMapper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 02.05.2018 7:56
 */
public class AttributeSelect extends DropDownChoice<Long> {
    @Inject
    private DomainMapper domainMapper;

    public AttributeSelect(String id, IModel<Long> model, String refEntityName, Long refEntityAttributeId) {
        super(id);

        setModel(model);

        Map<Long, Domain> map = domainMapper.getDomains(FilterWrapper.of(new Domain(refEntityName))).stream()
                .collect(Collectors.toMap(Domain::getObjectId, d -> d));

        setChoices(new ArrayList<>(map.keySet()));

        setChoiceRenderer(new IChoiceRenderer<Long>() {
            @Override
            public Object getDisplayValue(Long object) {
                return map.get(object).getValueText(refEntityAttributeId, getLocale());
            }

            @Override
            public String getIdValue(Long object, int index) {
                return object.toString();
            }

            @Override
            public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                return Long.parseLong(id);
            }
        });
    }

    @Override
    protected String getNullKeyDisplayValue() {
        return "";
    }
}
