package ru.complitex.domain.component.form;

import com.google.common.base.Strings;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelectConfig;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.util.Attributes;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author Anatoly A. Ivanov
 * 17.04.2018 0:01
 */
public class AttributeSelectList extends FormComponentPanel<Attribute> {
    @Inject
    private DomainMapper domainMapper;

    private ListModel<Long> listModel = new ListModel<>();

    private SerializableConsumer<AjaxRequestTarget> onChange;

    public AttributeSelectList(String id, IModel<Attribute> model, String refEntityName,
                               Long refEntityAttributeId, IModel<List<Long>> parentListModel, boolean upperCase) {
        super(id, model);

        setOutputMarkupId(true);

        WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        List<Domain> domains = domainMapper.getDomains(FilterWrapper.of(new Domain(refEntityName)));
        domains.removeIf(d -> Strings.isNullOrEmpty(d.getAttribute(refEntityAttributeId).getValue(getLocale()).getText()));
        domains.sort(Comparator.comparing(d -> d.getAttribute(refEntityAttributeId).getValue(getLocale()).getText()));

        Map<Long, String> names = domains.stream().collect(Collectors.toMap(Domain::getId,
                d -> {
                    String text = d.getTextValue(refEntityAttributeId, getLocale());

                    return getPrefix(d) + (upperCase ? Attributes.capitalize(text) : text);
                }));

        listModel.setObject(model.getObject().getNumberValues());

        ListView<Long> listView = new ListView<Long>("selects", listModel) {
            @Override
            protected void populateItem(ListItem<Long> item) {
                WebMarkupContainer group = new WebMarkupContainer("group");
                item.add(group);

                if (isEnabledInHierarchy()){
                    group.add( new BootstrapSelect<Long>("select", item.getModel(),
                            new LoadableDetachableModel<List<Long>>() {
                                @Override
                                protected List<Long> load() {
                                    return domains.stream()
                                            .filter(d -> parentListModel == null
                                                    || parentListModel.getObject().contains(d.getParentId())
                                                    || Objects.equals(d.getObjectId(), item.getModel().getObject()))
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
                                    return object.toString();
                                }

                                @Override
                                public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                                    return id != null && !id.isEmpty() ? Long.valueOf(id) : null;
                                }
                            }){
                        @Override
                        protected String getNullKeyDisplayValue() {
                            return "";
                        }
                    }.with(new BootstrapSelectConfig().withNoneSelectedText("")).add(OnChangeAjaxBehavior.onChange(
                            AttributeSelectList.this::onChange)));

                    group.add(new AjaxLink<Void>("remove") {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            listModel.getObject().remove(item.getIndex());

                            target.add(container);

                            onChange(target);
                        }
                    });

                    item.add(new EmptyPanel("view").setVisible(false));
                } else {
                    group.setVisible(false);

                    item.add(new TextField<>("view", Model.of(names.get(item.getModelObject()))));
                }
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

            @Override
            public boolean isVisible() {
                return isEnabledInHierarchy();
            }
        });
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void convertInput() {
        Attribute attribute = getModelObject();

        if (attribute != null){
            attribute.getValues().clear();

            listModel.getObject().forEach(n -> getModelObject().addNumberValue(n));
        }

        setConvertedInput(attribute);
    }

    public IModel<List<Long>> getListModel() {
        return listModel;
    }

    public AttributeSelectList(String id, IModel<Attribute> model, String referenceEntityName, Long referenceEntityAttributeId, boolean upperCase){
        this(id, model, referenceEntityName, referenceEntityAttributeId, null, upperCase);
    }

    protected void onChange(AjaxRequestTarget target){
        if (onChange != null) {
            onChange.accept(target);
        }
    }

    public void onChange(SerializableConsumer<AjaxRequestTarget> onChange) {
        this.onChange = onChange;
    }

    @Override
    public boolean checkRequired() {
        return !isRequired() || !listModel.getObject().isEmpty();
    }

    protected String getPrefix(Domain domain){
        return "";
    }
}
