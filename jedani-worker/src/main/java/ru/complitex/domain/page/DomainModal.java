package ru.complitex.domain.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.wicket.form.FormGroupBorder;
import ru.complitex.domain.component.form.DomainAutoComplete;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.model.DecimalAttributeModel;
import ru.complitex.domain.model.TextAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Domains;
import ru.complitex.domain.util.Locales;
import ru.complitex.jedani.worker.page.BasePage;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 26.02.2019 18:17
 */
public class DomainModal<T extends Domain> extends AbstractModal<T> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private EntityService entityService;

    @Inject
    private DomainService domainService;

    private final WebMarkupContainer container;
    private final NotificationPanel feedback;

    private final ListView<EntityAttribute> listView;

    private final Entity parentEntity;
    private final Entity entity;

    private final SerializableConsumer<AjaxRequestTarget> onChange;

    private final Long parentEntityAttributeId;

    private Class<T> domainClass;

    public <P extends Domain> DomainModal(String markupId, Class<T> domainClass, Class<P> parentClass, Long parentEntityAttributeId,
                                          List<EntityAttribute> entityAttributes, SerializableConsumer<AjaxRequestTarget> onChange) {
        super(markupId, Model.of(Domains.newObject(domainClass)));

        this.domainClass = domainClass;

        this.parentEntityAttributeId = parentEntityAttributeId;

        this.onChange = onChange;

        setBackdrop(Backdrop.FALSE);

        entity = entityService.getEntity(domainClass);

        header(Model.of(entity.getValue().getText()));

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true)
                .setOutputMarkupPlaceholderTag(true)
                .setVisible(false);
        add(container);

        feedback = new NotificationPanel("feedback");
        feedback.showRenderedMessages(false)
                .setOutputMarkupId(true);
        container.add(feedback);

        parentEntity = parentClass != null ? entityService.getEntity(parentClass) : null;

        if (parentClass != null) {
            FormGroupBorder parentGroup = new FormGroupBorder("parentGroup", Model.of(parentEntity.getValue().getText()));
            container.add(parentGroup);

            parentGroup.add(newParentComponent("parent", parentClass));
        }else{
            container.add(new EmptyPanel("parentGroup").setVisible(false));
        }

        listView = new ListView<EntityAttribute>("attributes",
                entityAttributes != null ? entityAttributes : entity.getAttributes()) {
            @Override
            protected void populateItem(ListItem<EntityAttribute> item) {
                EntityAttribute entityAttribute = item.getModelObject();
                T domain = DomainModal.this.getModelObject();

                Attribute attribute = domain.getOrCreateAttribute(entityAttribute.getEntityAttributeId());
                attribute.setEntityAttribute(entityAttribute);
                onAttribute(attribute);
                entityService.loadReference(attribute.getEntityAttribute());

                FormGroupBorder group = new FormGroupBorder("group", Model.of(entityAttribute.getValue().getText())){
                    @Override
                    protected boolean isRequired() {
                        return entityAttribute.isRequired();
                    }
                };
                FormComponent<?> input1 = null;
                FormComponent<?> input2 = null;
                Component component = newComponent("component", attribute);

                if (component == null) {
                    switch (entityAttribute.getValueType()){
                        case DECIMAL:
                            input1 = new TextField<>("input1", DecimalAttributeModel.of(attribute), BigDecimal.class);

                            break;
                        case TEXT:
                        case ENTITY_LIST:
                            input1 = new TextField<>("input1", new TextAttributeModel(attribute, entityAttribute.getStringType()));
                            break;
                        case DATE:
                            input1 = new DateTextField("input1",
                                    new PropertyModel<>(attribute, "date"),
                                    new DateTextFieldConfig().withFormat("dd.MM.yyyy").withLanguage("ru").autoClose(true));
                            break;
                        case ENTITY:
                            if (attribute.getEntityAttribute().getReferenceEntityAttributes() != null) {
                                EntityAttribute referenceEntityAttribute = attribute.getEntityAttribute().getReferenceEntityAttributes().get(0);

                                component = new DomainAutoComplete("component", referenceEntityAttribute.getDomainClass(),
                                        referenceEntityAttribute, new PropertyModel<>(attribute, "number"));
                            }
                            break;
                        case BOOLEAN:
                        case NUMBER:
                            input1 = new TextField<>("input1", new PropertyModel<>(attribute, "number"));
                            break;
                        case TEXT_LIST:
                            input1 = new TextField<>("input1", new TextAttributeModel(attribute.getOrCreateValue(
                                    Locales.getLocaleId(Locales.RU)), entityAttribute.getStringType()));
                            input1.add(new AttributeModifier("placeholder", getString("RU")));

                            input2 = new TextField<>("input2", new TextAttributeModel(attribute.getOrCreateValue(
                                    Locales.getLocaleId(Locales.UA)), entityAttribute.getStringType()));
                            input2.add(new AttributeModifier("placeholder", getString("UA")));

                            break;
                    }
                }

                IModel<String> labelModel = Model.of(entityAttribute.getValue().getText());

                if (input1 != null){
                    input1.setLabel(labelModel);
                    input1.setRequired(entityAttribute.isRequired());
                }
                if (input2 != null){
                    input2.setLabel(labelModel);
                }
                if (component instanceof FormComponent){
                    ((FormComponent<?>)component).setLabel(labelModel);
                    ((FormComponent<?>) component).setRequired(entityAttribute.isRequired());
                }

                group.add(input1 != null ? input1 : new EmptyPanel("input1").setVisible(false));
                group.add(input2 != null ? input2 : new EmptyPanel("input2").setVisible(false));
                group.add(component != null ? component : new EmptyPanel("component").setVisible(false));

                item.add(group);
            }
        };
        listView.setReuseItems(true);
        container.add(listView);

        addButton(new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                DomainModal.this.save(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(container);
            }
        }.setLabel(new ResourceModel("save")));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                DomainModal.this.cancel(target);
            }
        }.setLabel(new ResourceModel("cancel")));
    }

    protected Component newParentComponent(String componentId, Class<? extends Domain> parentClass) {
        return new DomainAutoComplete(componentId, parentClass, parentEntityAttributeId,
                new PropertyModel<>(getModel(), "parentId"));
    }

    protected void onAttribute(Attribute attribute){

    }

    protected Component newComponent(String componentId, Attribute attribute){
        return null;
    }

    protected T newDomain(){
        return Domains.newObject(domainClass);
    }

    @Override
    public void create(AjaxRequestTarget target){
       edit(newDomain(), target);
    }

    @Override
    public void edit(T domain, AjaxRequestTarget target){
        setModelObject(domain);

        listView.removeAll();
        container.setVisible(true);
        target.add(container);

        show(target);
    }

    private Long getCurrentUserId(){
        Page page = getPage();

        if (page instanceof BasePage){
            return ((BasePage) page).getCurrentUser().getId();
        }

        return null;
    }

    private void save(AjaxRequestTarget target){
        try {
            T domain = getModelObject();

            if (!DomainModal.this.validate(domain)){
                target.add(feedback);

                return;
            }

            domain.setUserId(getCurrentUserId());

            if (parentEntity != null){
                domain.setParentEntityId(parentEntity.getId());
            }

            domainService.save(domain);

            getSession().success(entity.getValue().getText() + " " + getString("info_saved"));

            close(target);

            if (onChange != null){
                onChange.accept(target);
            }
        } catch (Exception e) {
            log.error("error save domain", e);

            getSession().error("Ошибка сохранения " + e.getLocalizedMessage());

            target.add(feedback);
        }
    }

    public void cancel(AjaxRequestTarget target){
        container.setVisible(false);

        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent<?>) c).clearInput());

        close(target);
    }

    protected boolean validate(Domain domain){
        return true;
    }
}
