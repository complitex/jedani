package ru.complitex.domain.component.form;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.util.ListModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
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

    private SerializableConsumer<AjaxRequestTarget> onChange;

    public AttributeSelectList(String id, IModel<Attribute> model, String referenceEntityName,
                               Long referenceEntityAttributeId, IModel<List<Long>> parentListModel) {
        super(id, model);

        setOutputMarkupId(true);

        WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        List<Domain> domains = domainMapper.getDomains(FilterWrapper.of(new Domain(referenceEntityName)));
        domains.sort(Comparator.comparing(d -> d.getAttribute(referenceEntityAttributeId).getValue(getLocale()).getText()));

        Map<Long, String> names = domains.stream().collect(Collectors.toMap(Domain::getId,
                d -> d.getAttribute(referenceEntityAttributeId).getValue(getLocale()).getText()));

        List<Long> list;

        try {
            list = new ObjectMapper().readValue(model.getObject().getJson(), new TypeReference<List<Long>>(){});
        } catch (Exception e) {
            list = new ArrayList<>();
        }

        listModel.setObject(list);

        ListView<Long> listView = new ListView<Long>("selects", listModel) {
            @Override
            protected void populateItem(ListItem<Long> item) {
                item.add(new DropDownChoice<Long>("select", item.getModel(),
                        new LoadableDetachableModel<List<Long>>() {
                            @Override
                            protected List<Long> load() {
                                return domains.stream()
                                        .filter(d -> parentListModel == null ||
                                                parentListModel.getObject().contains(d.getParentId()))
                                        .map(Domain::getObjectId)
                                        .collect(Collectors.toList());
                            }
                        },
                        new IChoiceRenderer<Long>() {
                            @Override
                            public Object getDisplayValue(Long object) {
                                return names.get(object);
                            }

                            @Override
                            public String getIdValue(Long object, int index) {
                                return object + "";
                            }

                            @Override
                            public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                                return Long.parseLong(id);
                            }
                        }){
                    @Override
                    protected String getNullKeyDisplayValue() {
                        return "";
                    }
                }.add(OnChangeAjaxBehavior.onChange(AttributeSelectList.this::onChange)));

                item.add(new AjaxLink<Void>("remove") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        listModel.getObject().remove(item.getIndex());

                        target.add(container);

                        onChange(target);
                    }
                });
            }
        };
        listView.setReuseItems(false);
        container.add(listView);

        add(new AjaxLink<Void>("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                listModel.getObject().add(null);

                target.add(container);

                onChange(target);
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

    public IModel<List<Long>> getListModel() {
        return listModel;
    }

    public AttributeSelectList(String id, IModel<Attribute> model, String referenceEntityName, Long referenceEntityAttributeId){
        this(id, model, referenceEntityName, referenceEntityAttributeId, null);
    }

    protected void onChange(AjaxRequestTarget target){
        if (onChange != null) {
            onChange.accept(target);
        }
    }

    public void setOnChange(SerializableConsumer<AjaxRequestTarget> onChange) {
        this.onChange = onChange;
    }
}
