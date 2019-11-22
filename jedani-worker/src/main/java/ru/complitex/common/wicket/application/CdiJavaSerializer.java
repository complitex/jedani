package ru.complitex.common.wicket.application;

import org.apache.wicket.Component;
import org.apache.wicket.cdi.NonContextual;
import org.apache.wicket.serialize.java.JavaSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Field;

/**
 * @author Anatoly A. Ivanov
 * 21.11.2019 10:12 AM
 */
public class CdiJavaSerializer extends JavaSerializer {
    private Logger log = LoggerFactory.getLogger(CdiJavaSerializer.class);

    public CdiJavaSerializer(String applicationKey) {
        super(applicationKey);
    }

    @Override
    public Object deserialize(byte[] data) {
        Object o = super.deserialize(data);

        try {
            inject(o);
        } catch (Exception e) {
            log.error("deserialize error", e);
        }

        return o;
    }

    @Override
    public byte[] serialize(Object object) {
        Object o = super.deserialize(super.serialize(object));

        try {
            clearInject(o.getClass(), o);
        } catch (Exception e) {
            log.error("serialize error", e);
        }

        return super.serialize(o);
    }

    private void inject(Object object) throws IllegalAccessException {
        if (object == null){
            return;
        }

        NonContextual.of(object).inject(object);

        for (Field field : object.getClass().getDeclaredFields()){
            if (field.getType().getName().contains("ru.complitex.jedani") &&
                    Component.class.isAssignableFrom(field.getType()) &&
                    !field.getName().contains("$")){
                field.setAccessible(true);
                inject(field.get(object));
            }
        }
    }

    private void clearInject(Class<?> _class, Object object) throws IllegalAccessException {
        if (object == null){
            return;
        }

        for (Field field : _class.getDeclaredFields()){
            if (field.getAnnotation(Inject.class) != null){
                field.setAccessible(true);
                field.set(object, null);
            }

            if (field.getType().getName().contains("ru.complitex.jedani") &&
                    Component.class.isAssignableFrom(field.getType()) &&
                    !field.getName().contains("$")){
                field.setAccessible(true);
                clearInject(field.getType(), field.get(object));
            }
        }

        if (_class.getSuperclass() != null){
            clearInject(_class.getSuperclass(), object);
        }
    }
}
