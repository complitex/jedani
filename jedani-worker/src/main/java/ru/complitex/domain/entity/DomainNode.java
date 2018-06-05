package ru.complitex.domain.entity;

/**
 * @author Anatoly A. Ivanov
 * 04.06.2018 13:44
 */
public abstract class DomainNode extends Domain{
    private Long left;
    private Long right;
    private Long level;

    public DomainNode(String entityName) {
        super(entityName);
    }

    public DomainNode(Domain domain, String entityName) {
        super(domain, entityName);
    }

    public Long getLeft() {
        return left;
    }

    public void setLeft(Long left) {
        this.left = left;
    }

    public Long getRight() {
        return right;
    }

    public void setRight(Long right) {
        this.right = right;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }
}
