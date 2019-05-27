package ru.complitex.jedani.worker.page.sale;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelectConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.*;
import ru.complitex.common.wicket.form.AjaxSelectLabel;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.jedani.worker.entity.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SaleDecisionModal extends Modal<SaleDecision> {
    public SaleDecisionModal(String markupId) {
        super(markupId);

        setBackdrop(Backdrop.FALSE);
        size(Size.Large);

        header(new ResourceModel("header"));

        IModel<SaleDecision> saleDecisionModel = Model.of(new SaleDecision().addRule());

        Form form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);

        ListView<RuleCondition> conditions = new ListView<RuleCondition>("conditions",
                new PropertyModel<>(saleDecisionModel, "rules.0.conditions")) {
            @Override
            protected void populateItem(ListItem<RuleCondition> item) {
                item.add(new AjaxSelectLabel<Long>("type",
                        NumberAttributeModel.of(item.getModel(), RuleCondition.TYPE),
                        Arrays.asList(1L, 2L, 3L, 4L), new IChoiceRenderer<Long>() {
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
                    protected void onApply(AjaxRequestTarget target) {
                        saleDecisionModel.getObject().updateCondition(item.getModelObject().getIndex());

                        target.add(form);
                    }

                    @Override
                    protected void onRemove(AjaxRequestTarget target) {
                        saleDecisionModel.getObject().removeCondition((long) item.getIndex());

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
                saleDecisionModel.getObject().addCondition();

                target.add(form);
            }
        });

        ListView<RuleAction> actions = new ListView<RuleAction>("actions",
                new PropertyModel<>(saleDecisionModel, "rules.0.actions")) {
            @Override
            protected void populateItem(ListItem<RuleAction> item) {
                item.add(new AjaxSelectLabel<Long>("type",
                        NumberAttributeModel.of(item.getModel(), RuleAction.TYPE),
                        Arrays.asList(1L, 2L), new IChoiceRenderer<Long>() {
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
                    protected void onApply(AjaxRequestTarget target) {
                        saleDecisionModel.getObject().updateAction(item.getModelObject().getIndex());

                        target.add(form);
                    }

                    @Override
                    protected void onRemove(AjaxRequestTarget target) {
                        saleDecisionModel.getObject().removeAction((long) item.getIndex());

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
                saleDecisionModel.getObject().addAction();

                target.add(form);
            }
        });

        ListView<Rule> rules = new ListView<Rule>("rules",
                new PropertyModel<>(saleDecisionModel, "rules")) {
            @Override
            protected void populateItem(ListItem<Rule> item) {
                item.add(new Label("id", item.getIndex() + 1));

                item.add(new ListView<RuleCondition>("ruleConditions",
                        new PropertyModel<>(item.getModel(), "conditions")) {
                    @Override
                    protected void populateItem(ListItem<RuleCondition> item) {
                        RuleCondition ruleCondition = item.getModelObject();

                        item.add(new BootstrapSelect<Long>("comparator", NumberAttributeModel.of(item.getModel(),
                                RuleCondition.COMPARATOR), RuleConditionComparator.getComparatorIds(),
                                new IChoiceRenderer<Long>() {
                            @Override
                            public Object getDisplayValue(Long object) {
                                if (object != null){
                                    switch (object.intValue()){
                                        case (int) RuleConditionComparator.EQUAL: return "==";
                                        case (int) RuleConditionComparator.NOT_EQUAL: return "!=";
                                        case (int) RuleConditionComparator.GREATER: return ">";
                                        case (int) RuleConditionComparator.LOWER: return "<";
                                        case (int) RuleConditionComparator.GREATER_OR_EQUAL: return ">=";
                                        case (int) RuleConditionComparator.LOWER_OR_EQUAL: return "<=";
                                    }
                                }

                                return null;
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
                            public boolean isVisible() {
                                return hasComparator(ruleCondition);
                            }
                        }.with(new BootstrapSelectConfig().withNoneSelectedText("")));

                        item.add(new TextField<>("value", Model.of(ruleCondition.getIndex())));

                        //todo add value input
                    }
                });

                item.add(new ListView<RuleAction>("ruleActions",
                        new PropertyModel<>(item.getModel(), "actions")) {
                    @Override
                    protected void populateItem(ListItem<RuleAction> item) {
                        item.add(new Label("value", item.getModelObject().getIndex()));
                    }
                });
            }
        };
        form.add(rules);

        form.add(new AjaxLink<RuleCondition>("addRule") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                saleDecisionModel.getObject().addRule();

                target.add(form);
            }
        });
    }

    public boolean hasComparator(RuleCondition ruleCondition){
        return !Objects.equals(ruleCondition.getType(), RuleConditionType.PAYMENT_MONTHLY);

    }

    public void edit(AjaxRequestTarget target){
        appendShowDialogJavaScript(target);
    }
}
