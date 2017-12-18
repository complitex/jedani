package ru.complitex.domain.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormGroup;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.mapper.EntityMapper;
import ru.complitex.jedani.user.web.BasePage;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 17.12.2017 21:51
 */
public abstract class DomainEditPage extends BasePage{
    @Inject
    private EntityMapper entityMapper;

    @Inject
    private DomainMapper domainMapper;

    public DomainEditPage(String entityName, PageParameters parameters, Class<? extends WebPage> backPage) {
        Entity entity = entityMapper.getEntity(entityName);

        add(new Label("header", entity.getValue().getText()));

        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        BootstrapForm form = new BootstrapForm("form");
        add(form);

        Domain domain = domainMapper.getDomain(entityName, parameters.get("id").toLongObject());

        if (domain == null){
            throw new WicketRuntimeException("domain not found");
        }

        ListView listView = new ListView<EntityAttribute>("attributes", entity.getAttributes()) {
            @Override
            protected void populateItem(ListItem<EntityAttribute> item) {
                EntityAttribute entityAttribute = item.getModelObject();
                Attribute attribute = domain.getOrCreateAttribute(entityAttribute.getId());

                FormGroup group = new FormGroup("group", Model.of(entityAttribute.getValue().getText()));
                FormComponent input1 = null;
                FormComponent input2 = null;

                switch (entityAttribute.getValueType()){
                    case STRING:
                    case DECIMAL:
                        input1 = new TextField<>("input1", new PropertyModel<>(attribute, "text"));
                        break;
                    case DATE:
                        input1 = new TextField<>("input1", new PropertyModel<>(attribute, "text"));
                        break;
                    case INTEGER:
                        input1 = new TextField<>("input1", new PropertyModel<>(attribute, "number"));
                        break;
                    case VALUE:
                        input1 = new TextField<>("input1", new PropertyModel<>(attribute.getOrCreateValue(1L), "text"));
                        input2 = new TextField<>("input2", new PropertyModel<>(attribute.getOrCreateValue(2L), "text"));
                        break;
                }

                group.add(input1);
                group.add(input2 != null ? input2 : new EmptyPanel("input2").setVisible(false));

                item.add(group);
            }
        };
        listView.setReuseItems(true);
        form.add(listView);



        //todo dev submit -> validate -> update
    }
}
