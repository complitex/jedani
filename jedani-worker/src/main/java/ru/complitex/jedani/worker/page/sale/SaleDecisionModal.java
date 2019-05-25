package ru.complitex.jedani.worker.page.sale;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import ru.complitex.jedani.worker.entity.Rule;
import ru.complitex.jedani.worker.entity.RuleAction;
import ru.complitex.jedani.worker.entity.RuleCondition;
import ru.complitex.jedani.worker.entity.SaleDecision;

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
        add(form);

        ListView<RuleCondition> conditions = new ListView<RuleCondition>("conditions",
                new PropertyModel<>(saleDecisionModel, "header.conditions")) {
            @Override
            protected void populateItem(ListItem<RuleCondition> item) {

            }
        };
        form.add(conditions);

        ListView<RuleAction> actions = new ListView<RuleAction>("actions",
                new PropertyModel<>(saleDecisionModel, "header.actions")) {
            @Override
            protected void populateItem(ListItem<RuleAction> item) {

            }
        };
        form.add(actions);

        ListView<Rule> rules = new ListView<Rule>("rules",
                new PropertyModel<>(saleDecisionModel, "header.rules")) {
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
