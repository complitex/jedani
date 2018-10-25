package ru.complitex.domain.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormGroup;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.domain.component.form.DomainAutoComplete;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.mapper.EntityMapper;
import ru.complitex.domain.model.TextAttributeModel;
import ru.complitex.domain.util.Locales;
import ru.complitex.jedani.worker.page.BasePage;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static ru.complitex.domain.model.TextAttributeModel.TYPE.UPPER_CASE;

/**
 * @author Anatoly A. Ivanov
 * 17.12.2017 21:51
 */
public abstract class DomainEditPage<T extends Domain> extends BasePage{
    private Logger log = LoggerFactory.getLogger(getClass());

    public static final String COMPONENT_WICKET_ID = "component";

    @Inject
    private EntityMapper entityMapper;

    @Inject
    private DomainMapper domainMapper;

    private Form form;

    public DomainEditPage(Class<T> domainClass, PageParameters parameters, Class<? extends WebPage> backPage) {
        T domainInstance;

        try {
            domainInstance = domainClass.newInstance();
        } catch (Exception e) {
            throw new WicketRuntimeException(e);
        }

        Entity entity = entityMapper.getEntity(domainInstance.getEntityName());

        String title = entity.getValue().getText();

        add(new Label("title", title));

        add(new Label("header", title));

        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        form = new BootstrapForm("form");
        add(form);

        Long objectId = parameters.get("id").toOptionalLong();

        Domain domain = objectId != null ? domainMapper.getDomain(domainInstance.getEntityName(), objectId,
                domainInstance.isUseDateAttribute(), domainInstance.isUseNumberValue()) : domainInstance;

        if (domain == null){
            throw new WicketRuntimeException("domain not found");
        }

        //Parent

        Entity parentEntity = getParentEntityName() != null ? entityMapper.getEntity(getParentEntityName()) : null;

        if (parentEntity != null) {
            FormGroup parentGroup = new FormGroup("parentGroup", Model.of(parentEntity.getValue().getText()));
            form.add(parentGroup);

            parentGroup.add(new DomainAutoComplete("parent", parentEntity.getEntityAttribute(getParentEntityAttributeId()),
                    new PropertyModel<>(domain, "parentId")));
        }else{
            form.add(new EmptyPanel("parentGroup").setVisible(false));
        }

        //Attributes

        List<Long> entityAttributeIds = getEntityAttributeIds();

        List<EntityAttribute> entityAttributes = entityAttributeIds != null
                ? entityAttributeIds.stream().map(entity::getEntityAttribute).collect(Collectors.toList())
                : entity.getAttributes();

        ListView listView = new ListView<EntityAttribute>("attributes", entityAttributes) {
            @Override
            protected void populateItem(ListItem<EntityAttribute> item) {
                EntityAttribute entityAttribute = item.getModelObject();

                Attribute attribute = domain.getOrCreateAttribute(entityAttribute.getEntityAttributeId());
                attribute.setEntityAttribute(entityAttribute);
                onAttribute(attribute);

                FormGroup group = new FormGroup("group", Model.of(entityAttribute.getValue().getText()));
                FormComponent input1 = null;
                FormComponent input2 = null;
                Component component = getComponent(attribute);

                if (component == null) {
                    switch (entityAttribute.getValueType()){
                        case TEXT:
                        case DECIMAL:
                        case BOOLEAN:
                        case ENTITY_VALUE:
                            input1 = new TextField<>("input1", new TextAttributeModel(attribute, UPPER_CASE));
                            break;
                        case DATE:
                            input1 = new TextField<>("input1", new PropertyModel<>(attribute, "date"));
                            break;
                        case ENTITY:
                            component = new DomainAutoComplete(COMPONENT_WICKET_ID,
                                    attribute.getEntityAttribute().getReferenceEntityAttribute(),
                                    new PropertyModel<>(attribute, "number"));
                            break;
                        case NUMBER:
                            input1 = new TextField<>("input1", new PropertyModel<>(attribute, "number"));
                            break;
                        case TEXT_VALUE:
                            input1 = new TextField<>("input1", new TextAttributeModel(attribute.getOrCreateValue(
                                    Locales.getLocaleId(Locales.RU)), UPPER_CASE));
                            input1.add(new AttributeModifier("placeholder", getString("RU")));

                            input2 = new TextField<>("input2", new TextAttributeModel(attribute.getOrCreateValue(
                                    Locales.getLocaleId(Locales.UA)), UPPER_CASE));
                            input2.add(new AttributeModifier("placeholder", getString("UA")));

                            break;
                    }
                }

                group.add(input1 != null ? input1 : new EmptyPanel("input1").setVisible(false));
                group.add(input2 != null ? input2 : new EmptyPanel("input2").setVisible(false));
                group.add(component != null ? component : new EmptyPanel(COMPONENT_WICKET_ID).setVisible(false));

                item.add(group);
            }
        };
        listView.setReuseItems(true);
        form.add(listView);

        BootstrapAjaxButton save = new BootstrapAjaxButton("save", Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    domain.setEntityName(domainInstance.getEntityName());
                    domain.setUserId(getCurrentUser().getId());

                    if (parentEntity != null){
                        domain.setParentEntityId(parentEntity.getId());
                    }

                    if (domain.getObjectId() != null){
                        domainMapper.updateDomain(domain);
                    }else{
                        domainMapper.insertDomain(domain);
                    }

                    getSession().info(entity.getValue().getText() + " " + getString("info_saved"));

                    if (backPage != null) {
                        setResponsePage(backPage);
                    }else {
                        target.add(feedback);
                    }
                } catch (Exception e) {
                    log.error("error save domain", e);

                    getSession().error("Ошибка сохранения " + e.getLocalizedMessage());

                    target.add(feedback);
                }
            }
        };
        save.setLabel(new ResourceModel("save"));
        form.add(save);

        BootstrapAjaxButton cancel = new BootstrapAjaxButton("cancel", Buttons.Type.Default) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                setResponsePage(backPage);
            }
        };
        cancel.setLabel(new ResourceModel("cancel"));
        cancel.setDefaultFormProcessing(false);
        form.add(cancel);

        //todo validate -> entity select
    }

    protected String getParentEntityName(){
        return null;
    }

    protected Long getParentEntityAttributeId(){
        return null;
    }

    protected List<Long> getEntityAttributeIds(){
        return null;
    }

    protected boolean isUpperCaseNames(){
        return true;
    }

    protected void onAttribute(Attribute attribute){
    }

    protected Component getComponent(Attribute attribute){
        return null;
    }

    protected String getPrefix(EntityAttribute entityAttribute, Domain domain){
        return "";
    }

    public Form getForm() {
        return form;
    }
}
