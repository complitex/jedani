package ru.complitex.domain.component.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Value;


/**
 * @author Anatoly A. Ivanov
 * 09.01.2019 21:30
 */
public abstract class AbstractDomainAutoCompleteList extends Panel {
    public AbstractDomainAutoCompleteList(String id, String entityName, IModel<Attribute> model) {
        super(id, model);

        setOutputMarkupId(true);

        WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        ListView<Value> listView = new ListView<Value>("items", new PropertyModel<>(model, "values")){

            @Override
            protected void populateItem(ListItem<Value> item) {
                item.add(new AbstractDomainAutoComplete("item", entityName,
                        new PropertyModel<>(item.getModel(), "number"),
                        AbstractDomainAutoCompleteList.this::onUpdate){
                    @Override
                    protected Domain getFilterObject(String input) {
                        return AbstractDomainAutoCompleteList.this.getFilterObject(input);
                    }

                    @Override
                    protected String getTextValue(Domain domain) {
                        return  AbstractDomainAutoCompleteList.this.getTextValue(domain);
                    }
                });

                item.add(new AjaxLink<Void>("remove") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        model.getObject().getValues().remove(item.getIndex());

                        target.add(container);
                    }

                    @Override
                    public boolean isVisible() {
                        return AbstractDomainAutoCompleteList.this.isEnabled();
                    }
                });

            }
        };
        listView.setReuseItems(false);
        container.add(listView);

        add(new AjaxLink<Void>("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                model.getObject().getValues().add(new Value());

                target.add(container);
            }

            @Override
            public boolean isVisible() {
                return AbstractDomainAutoCompleteList.this.isEnabled();
            }
        });
    }

    protected abstract String getTextValue(Domain domain);

    protected abstract Domain getFilterObject(String input);

    protected void onUpdate(AjaxRequestTarget target){

    }

}
