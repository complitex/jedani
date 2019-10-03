package ru.complitex.common.model;

import org.apache.wicket.model.util.ListModel;

import java.util.Arrays;

/**
 * @author Anatoly A. Ivanov
 * 03.10.2019 11:36 PM
 */
public class ArrayListModel<T> extends ListModel<T> {
    public ArrayListModel(T... array) {
        super(Arrays.asList(array));
    }
}
