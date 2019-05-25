package ru.complitex.common.wicket.form;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelectConfig;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import ru.complitex.jedani.worker.entity.RuleAction;

import java.util.List;

public class AjaxSelectLabel<T> extends FormComponentPanel<T> {
    private boolean edit = false;

    public AjaxSelectLabel(String id, IModel<T> model, List<? extends T> choices, IChoiceRenderer<T> renderer) {
        super(id, model);

        WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        WebMarkupContainer selectContainer = new WebMarkupContainer("selectContainer"){
            @Override
            public boolean isVisible() {
                return model.getObject() == null || edit;
            }
        };
        container.add(selectContainer);

        BootstrapSelect select = new BootstrapSelect<>("select", model, choices, renderer);
        select.with(new BootstrapSelectConfig().withNoneSelectedText(""));
        select.add(OnChangeAjaxBehavior.onChange(t -> {}));
        selectContainer.add(select);

        selectContainer.add(new AjaxLink<RuleAction>("remove") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                AjaxSelectLabel.this.onRemove(target);
            }
        });

        selectContainer.add(new AjaxLink<T>("apply") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                edit = false;

                target.add(container);

                AjaxSelectLabel.this.onApply(target);
            }

            @Override
            public boolean canCallListener() {
                return AjaxSelectLabel.this.isEnabledInHierarchy();
            }
        });

        AjaxLink link = new AjaxLink<T>("link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                edit = true;

                target.add(container);
            }

            @Override
            public boolean isVisible() {
                return model.getObject() != null && !edit;
            }

            @Override
            public boolean canCallListener() {
                return AjaxSelectLabel.this.isEnabledInHierarchy();
            }
        };
        container.add(link);

        Label label = new Label("label", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                String s = renderer.getDisplayValue(model.getObject()) + "";

                return !s.isEmpty() ? s : "-";
            }
        });
        link.add(label);
    }

    protected void onApply(AjaxRequestTarget target) {

    }

    protected void onRemove(AjaxRequestTarget target) {

    }
}
