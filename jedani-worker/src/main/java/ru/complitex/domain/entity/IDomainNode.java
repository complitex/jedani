package ru.complitex.domain.entity;

/**
 * @author Anatoly A. Ivanov
 * 04.06.2018 13:34
 */
public interface IDomainNode {
    Long getLeft();
    void setLeft(Long left);

    Long getRight();
    void setRight(Long right);

    Long getLevel();
    void setLevel(Long level);

    Long getNodeParentId();
}
