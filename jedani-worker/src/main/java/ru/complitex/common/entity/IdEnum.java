package ru.complitex.common.entity;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 15:35
 */
public interface IdEnum<T extends IdEnum> extends Serializable{
    Long getId();
}
