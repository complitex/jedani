package ru.complitex.domain.component.form;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelectConfig;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 13.12.2018 20:54
 */
public class RoleSelectList extends Panel {
    public RoleSelectList(String id, IModel<List<String>> model, List<String> roles) {
        super(id, model);

        setOutputMarkupId(true);

        WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        ListView<String> listView = new ListView<String>("selects", model) {
            @Override
            protected void populateItem(ListItem<String> item) {
                List<String> list = roles.stream()
                        .filter(r -> item.getModel().getObject() != null || !model.getObject().contains(r))
                        .collect(Collectors.toList());

                item.add(new BootstrapSelect<>("select", item.getModel(), list,
                        new IChoiceRenderer<String>() {
                            @Override
                            public Object getDisplayValue(String object) {
                                return RoleSelectList.this.getDisplayValue(object);
                            }

                            @Override
                            public String getIdValue(String object, int index) {
                                return object;
                            }

                            @Override
                            public String getObject(String id, IModel<? extends List<? extends String>> choices) {
                                return id;
                            }
                        }).with(new BootstrapSelectConfig()
                        .withNoneSelectedText(""))
                        .setNullValid(false)
                        .add(OnChangeAjaxBehavior.onChange(
                                RoleSelectList.this::onChange)));

                item.add(new AjaxLink<Void>("remove") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        model.getObject().remove(item.getIndex());

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
                model.getObject().add(null);

                target.add(container);
            }
        });
    }

    protected void onChange(AjaxRequestTarget target) {

    }

    protected String getDisplayValue(String object){
        return object;
    }
}
