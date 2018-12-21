package ru.complitex.jedani.worker.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import ru.complitex.domain.entity.Attribute;

import java.util.Objects;


/**
 * @author Anatoly A. Ivanov
 * 19.10.2018 23:09
 */
public class WorkerAutoCompleteList extends FormComponentPanel<Attribute> {
    private ListModel<Long> listModel = new ListModel<>();

    public WorkerAutoCompleteList(String id, IModel<Attribute> model) {
        super(id, model);

        setOutputMarkupId(true);

        WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        listModel.setObject(model.getObject().getNumberValues());

        ListView<Long> listView = new ListView<Long>("items", listModel){

            @Override
            protected void populateItem(ListItem<Long> item) {
                item.add(new WorkerAutoComplete("item", item.getModel(), target -> {}));

                item.add(new AjaxLink<Void>("remove") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        listModel.getObject().remove(item.getIndex());

                        target.add(container);
                    }

                    @Override
                    public boolean isVisible() {
                        return WorkerAutoCompleteList.this.isEnabled();
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
            }

            @Override
            public boolean isVisible() {
                return WorkerAutoCompleteList.this.isEnabled();
            }
        });
    }

    @Override
    public void convertInput() {
        Attribute attribute = getModelObject();

        if (attribute != null){
            attribute.getValues().clear();

            listModel.getObject().forEach(n -> getModelObject().addNumberValue(n));
        }

        setConvertedInput(attribute);
    }

    @Override
    public boolean checkRequired() {
        if (isRequired()){
            return listModel.getObject().stream().anyMatch(Objects::nonNull);
        }

        return true;
    }
}
