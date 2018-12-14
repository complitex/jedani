package ru.complitex.domain.component.form;

import com.google.common.base.Strings;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelectConfig;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.util.Attributes;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 02.05.2018 7:56
 */
public class AttributeSelect extends BootstrapSelect<Long> {
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
                return Attributes.capitalize(map.get(object).getValueText(refEntityAttributeId, getLocale()));
            }

            @Override
            public String getIdValue(Long object, int index) {
                return object.toString();
            }

            @Override
            public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                return !Strings.isNullOrEmpty(id) ? Long.parseLong(id) : null;
            }
        });

        with(new BootstrapSelectConfig().withNoneSelectedText(""));
    }

    @Override
    protected String getNullKeyDisplayValue() {
        return "";
    }
}
