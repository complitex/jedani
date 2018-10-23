package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.domain.component.form.DomainAutoComplete;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.page.DomainEditPage;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.Product;
import ru.complitex.jedani.worker.mapper.ProductMapper;
import ru.complitex.jedani.worker.security.JedaniRoles;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 22.10.2018 16:17
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class ProductEditPage extends DomainEditPage<Product> {
    @Inject
    private EntityService entityService;

    @Inject
    private ProductMapper productMapper;

    public ProductEditPage(PageParameters parameters) {
        super(Product.class, parameters, ProductListPage.class);
    }

    @Override
    protected Component getComponent(Attribute attribute) {
        if (Objects.equals(attribute.getEntityAttributeId(), Product.NOMENCLATURE_ID)){
           return new DomainAutoComplete<Product>(COMPONENT_WICKET_ID, Product.class, attribute.getEntityAttribute(),
                   new PropertyModel<>(attribute, "number")){
               @Override
               protected List<Product> getDomains(String input) {
                   return productMapper.getProducts(input);
               }

               @Override
               protected String getTextValue(Product product) {
                   return super.getTextValue(product);
               }
           };
        }


        if (attribute.getEntityAttributeId().equals(Product.STORAGE_ID) ||
                attribute.getEntityAttributeId().equals(Product.STORAGE_INTO_ID)){
            return new EmptyPanel(COMPONENT_WICKET_ID); //todo

        }

        return null;
    }
}
