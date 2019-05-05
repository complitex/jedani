package ru.complitex.common.model;

import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.FilterWrapper;

/**
 * @author Anatoly A. Ivanov
 * 07.12.2018 19:34
 */
public class FilterMapModel<T> implements IModel<T> {
    private final String filter;
    private final IModel model;

    public FilterMapModel(IModel<FilterWrapper> model, String filter) {
        this.model = model;
        this.filter = filter;
    }

    @SuppressWarnings("unchecked")
    public static <T> FilterMapModel<T> of(IModel model, String filter){
        return new FilterMapModel<>(model, filter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() {
        return (T) ((FilterWrapper)model.getObject()).getMap().get(filter);
    }

    @Override
    public void setObject(T object) {
        ((FilterWrapper)model.getObject()).put(filter, object);
    }
}
