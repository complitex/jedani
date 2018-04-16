package ru.complitex.domain.component.form;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisitor;
import ru.complitex.domain.entity.Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 15.04.2018 22:35
 */
public class AttributeInputList extends FormComponentPanel<Attribute> {
    public AttributeInputList(String id, IModel<Attribute> model) {
        super(id, model);

        String json = model.getObject().getJson();

        List<String> list;

        try {
            list = new ObjectMapper().readValue(json, new TypeReference<List<String>>(){});
        } catch (Exception e) {
            list = new ArrayList<>();

            if (json != null && !json.trim().isEmpty()){
                list.add(json);
            }
        }

        ListView<String> listView = new ListView<String>("inputs", list) {
            @Override
            protected void populateItem(ListItem<String> item) {
                item.add(new TextField<>("input", item.getModel()));
            }
        };

        add(listView);
    }

    @Override
    public void convertInput() {
        ArrayNode array = new ObjectMapper().createArrayNode();

        visitChildren(TextField.class, (IVisitor<TextField<String>, Object>) (textField, iVisit) -> {
            array.add(textField.getConvertedInput());
        });

        Attribute attribute = getModelObject();

        attribute.setJson(array.toString());

        setConvertedInput(attribute);
    }
}


