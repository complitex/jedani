package ru.complitex.domain.component.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import ru.complitex.domain.entity.Attribute;

/**
 * @author Anatoly A. Ivanov
 * 15.04.2018 22:35
 */
public class AttributeInputList extends FormComponentPanel<Attribute> {
    private ListModel<String> listModel = new ListModel<>();

    public AttributeInputList(String id, IModel<Attribute> model) {
        super(id, model);

        listModel.setObject(model.getObject().getTextValues());

        WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        ListView<String> listView = new ListView<String>("inputs", listModel) {
            @Override
            protected void populateItem(ListItem<String> item) {
                WebMarkupContainer group = new WebMarkupContainer("group");
                item.add(group);

                if (isEnabledInHierarchy()) {
                    group.add(new TextField<>("input", item.getModel()).add(OnChangeAjaxBehavior.onChange(t -> {})));

                    group.add(new AjaxLink<Void>("remove") {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            listModel.getObject().remove(item.getIndex());

                            target.add(container);
                        }
                    });

                    item.add(new EmptyPanel("view").setVisible(false));
                }else{
                    group.setVisible(false);
                    item.add(new TextField<>("view", item.getModel()).setEnabled(false));
                }
            }
        };
        listView.setReuseItems(false);
        container.add(listView);

        add(new AjaxLink<Void>("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                listModel.getObject().add("");

                target.add(container);
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

            listModel.getObject().forEach(attribute::addTextValue);
        }

        setConvertedInput(attribute);
    }

    @Override
    public boolean checkRequired() {
        return !isRequired() || !listModel.getObject().isEmpty();
    }
}


