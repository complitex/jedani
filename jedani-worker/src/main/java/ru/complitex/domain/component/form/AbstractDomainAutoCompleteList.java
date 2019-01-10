package ru.complitex.domain.component.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;

import java.util.Objects;


/**
 * @author Anatoly A. Ivanov
 * 19.10.2018 23:09
 */
public abstract class AbstractDomainAutoCompleteList extends FormComponentPanel<Attribute> {
    private ListModel<Long> listModel = new ListModel<>();

    public AbstractDomainAutoCompleteList(String id, String entityName, IModel<Attribute> model) {
        super(id, model);

        setOutputMarkupId(true);

        WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        listModel.setObject(model.getObject().getNumberValues()); //todo sync attribute model

        ListView<Long> listView = new ListView<Long>("items", listModel){

            @Override
            protected void populateItem(ListItem<Long> item) {
                item.add(new AbstractDomainAutoComplete("item", entityName, item.getModel(), target -> {}){
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
                        listModel.getObject().remove(item.getIndex());

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
                listModel.getObject().add(null);

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

    @Override
    public boolean checkRequired() {
        if (isRequired()){
            return listModel.getObject().stream().anyMatch(Objects::nonNull);
        }

        return true;
    }
}
