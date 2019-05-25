package ru.complitex.jedani.worker.page.sale;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import ru.complitex.common.wicket.form.AjaxSelectLabel;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.jedani.worker.entity.Rule;
import ru.complitex.jedani.worker.entity.RuleAction;
import ru.complitex.jedani.worker.entity.RuleCondition;
import ru.complitex.jedani.worker.entity.SaleDecision;

import java.util.Arrays;
import java.util.List;

public class SaleDecisionModal extends Modal<SaleDecision> {
    public SaleDecisionModal(String markupId) {
        super(markupId);

        setBackdrop(Backdrop.FALSE);
        size(Size.Large);

        IModel<SaleDecision> saleDecisionModel = Model.of(new SaleDecision()
                .setHeader(new Rule()
                        .add(new RuleCondition())
                        .add(new RuleAction())));

        Form form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);

        ListView<RuleCondition> conditions = new ListView<RuleCondition>("conditions",
                new LoadableDetachableModel<List<RuleCondition>>() {
                    @Override
                    protected List<RuleCondition> load() {
                        return saleDecisionModel.getObject().getHeader().getConditions();
                    }
                }) {
            @Override
            protected void populateItem(ListItem<RuleCondition> item) {
                item.add(new AjaxSelectLabel<Long>("type",
                        NumberAttributeModel.of(item.getModel(), RuleCondition.TYPE),
                        Arrays.asList(1L, 2L, 3L), new IChoiceRenderer<Long>() {
                    @Override
                    public Object getDisplayValue(Long object) {
                        return getString("ruleConditionType." + object);
                    }

                    @Override
                    public String getIdValue(Long object, int index) {
                        return object + "";
                    }

                    @Override
                    public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                        return StringUtils.isNumeric(id) ? Long.valueOf(id) : null;
                    }
                }){
                    @Override
                    protected void onRemove(AjaxRequestTarget target) {
                        saleDecisionModel.getObject().getHeader().getConditions().remove(item.getModelObject());

                        target.add(form);
                    }
                });
            }
        };
        conditions.setReuseItems(false);
        form.add(conditions);

        form.add(new AjaxLink<RuleCondition>("addCondition") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                saleDecisionModel.getObject().getHeader().getConditions().add(new RuleCondition());

                target.add(form);
            }
        });

        ListView<RuleAction> actions = new ListView<RuleAction>("actions",
                new PropertyModel<>(saleDecisionModel, "header.actions")) {
            @Override
            protected void populateItem(ListItem<RuleAction> item) {
                item.add(new AjaxSelectLabel<Long>("type",
                        NumberAttributeModel.of(item.getModel(), RuleAction.TYPE),
                        Arrays.asList(1L, 2L, 3L), new IChoiceRenderer<Long>() {
                    @Override
                    public Object getDisplayValue(Long object) {
                        return getString("ruleActionType." + object);
                    }

                    @Override
                    public String getIdValue(Long object, int index) {
                        return object + "";
                    }

                    @Override
                    public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                        return StringUtils.isNumeric(id) ? Long.valueOf(id) : null;
                    }
                }){
                    @Override
                    protected void onRemove(AjaxRequestTarget target) {
                        saleDecisionModel.getObject().getHeader().getActions().remove(item.getModelObject());

                        target.add(form);
                    }
                });
            }
        };
        actions.setReuseItems(false);
        form.add(actions);

        form.add(new AjaxLink<RuleCondition>("addAction") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                saleDecisionModel.getObject().getHeader().getActions().add(new RuleAction());

                target.add(form);
            }
        });

        ListView<Rule> rules = new ListView<Rule>("rules",
                new PropertyModel<>(saleDecisionModel, "rules")) {
            @Override
            protected void populateItem(ListItem<Rule> item) {

            }
        };
        form.add(rules);

    }

    public void edit(AjaxRequestTarget target){
        appendShowDialogJavaScript(target);
    }
}
