package ru.complitex.domain.component.form;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import ru.complitex.domain.entity.Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 15.04.2018 22:35
 */
public class AttributeInputList extends FormComponentPanel<Attribute> {
    private ListModel<String> listModel = new ListModel<>();

    public AttributeInputList(String id, IModel<Attribute> model) {
        super(id, model);

        List<String> list;

        try {
            list = new ObjectMapper().readValue(model.getObject().getJson(), new TypeReference<List<String>>(){});
        } catch (Exception e) {
            list = new ArrayList<>();
        }

        listModel.setObject(list);

        WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        ListView<String> listView = new ListView<String>("inputs", listModel) {
            @Override
            protected void populateItem(ListItem<String> item) {
                item.add(new TextField<>("input", item.getModel()).add(OnChangeAjaxBehavior.onChange(t -> {})));

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
                listModel.getObject().add("");

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


