package ru.complitex.jedani.worker.page.sale;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.SaleItem;
import ru.complitex.jedani.worker.security.JedaniRoles;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 18.02.2019 15:22
 */
@AuthorizeInstantiation({JedaniRoles.AUTHORIZED})
public class SaleListPage extends DomainListPage<SaleItem> {
    private SaleModal saleModal;

    public SaleListPage() {
        super(SaleItem.class);

        Form saleForm = new Form("saleForm");
        getContainer().add(saleForm);

        saleModal = new SaleModal("sale"){
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                //todo on update
            }
        };
        saleForm.add(saleModal);
    }


    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(SaleItem.NOMENCLATURE).withReference(Nomenclature.ENTITY_NAME, Nomenclature.NAME));
        list.add(entity.getEntityAttribute(SaleItem.QUANTITY));
        list.add(entity.getEntityAttribute(SaleItem.PRICE));
        list.add(entity.getEntityAttribute(SaleItem.STORAGE));
        list.add(entity.getEntityAttribute(SaleItem.INSTALLMENT_PERCENTAGE));
        list.add(entity.getEntityAttribute(SaleItem.INSTALLMENT_MONTHS));

        return list;
    }

    @Override
    protected void onAddColumns(List<IColumn<SaleItem, SortProperty>> iColumns) {

    }

    @Override
    protected boolean isShowHeader() {
        return false;
    }

    @Override
    protected void onAdd(AjaxRequestTarget target) {
        saleModal.sale(target);
    }
}
