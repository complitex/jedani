package ru.complitex.domain.component.form;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.mapper.EntityAttributeMapper;

import javax.inject.Inject;


/**
 * @author Anatoly A. Ivanov
 * 17.04.2018 0:01
 */
public class AttributeSelectList extends FormComponentPanel<Attribute> {
    @Inject
    private transient EntityAttributeMapper entityAttributeMapper;

    private ListModel<Long> listModel = new ListModel<>();

    public AttributeSelectList(String id, IModel<Attribute> model) {
        super(id, model);

        WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);



        model.getObject().getEntityAttributeId();

        ListView<Long> listView = new ListView<Long>("listView", listModel) {
            @Override
            protected void populateItem(ListItem<Long> item) {
//                item.add(new DropDownChoice<Long>("choice", item.getModel(), ));

            }
        };
        listView.setReuseItems(false);
        listView.setOutputMarkupId(true);
        container.add(listView);

        DropDownChoice<Long> select = new DropDownChoice<>("select");
    }
}
