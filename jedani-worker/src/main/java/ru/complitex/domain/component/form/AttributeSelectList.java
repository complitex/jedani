package ru.complitex.domain.component.form;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.mapper.DomainMapper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author Anatoly A. Ivanov
 * 17.04.2018 0:01
 */
public class AttributeSelectList extends FormComponentPanel<Attribute> {
    @Inject
    private transient DomainMapper domainMapper;

    private ListModel<Long> listModel = new ListModel<>();

    public AttributeSelectList(String id, IModel<Attribute> model, String referenceEntityName, Long referenceEntityAttributeId) {
        super(id, model);

        WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        List<Domain> list = domainMapper.getDomains(FilterWrapper.of(new Domain(referenceEntityName)));
        list.sort(Comparator.comparing(d -> d.getAttribute(referenceEntityAttributeId).getValue(getLocale()).getText()));

        List<Long> listIds = list.stream().map(Domain::getObjectId).collect(Collectors.toList());

        Map<Long, String> names = list.stream().collect(Collectors.toMap(Domain::getId,
                d -> d.getAttribute(referenceEntityAttributeId).getValue(getLocale()).getText()));

        listModel.setObject(new ArrayList<>());

        listModel.getObject().add(model.getObject().getNumber());

        ListView<Long> listView = new ListView<Long>("selects", listModel) {
            @Override
            protected void populateItem(ListItem<Long> item) {
                item.add(new DropDownChoice<>("select", item.getModel(), listIds, new IChoiceRenderer<Long>() {
                    @Override
                    public Object getDisplayValue(Long object) {
                        return names.get(object);
                    }

                    @Override
                    public String getIdValue(Long object, int index) {
                        return index + "";
                    }

                    @Override
                    public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                        return listIds.get(Integer.parseInt(id));
                    }
                }).add(OnChangeAjaxBehavior.onChange(t -> {})));

                item.add(new AjaxLink<Void>("remove") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        listModel.getObject().remove(item.getIndex());

                        target.add(container);
                    }
                });
            }
        };
        listView.setReuseItems(false);
        container.add(listView);

        add(new AjaxLink<Void>("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                listModel.getObject().add(listIds.get(0));

                target.add(container);
            }
        });
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void convertInput() {
        ArrayNode array = new ObjectMapper().createArrayNode();

        listModel.getObject().forEach(array::add);

        Attribute attribute = getModelObject();

        attribute.setJson(array.toString());

        setConvertedInput(attribute);
    }
}
