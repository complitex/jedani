package ru.complitex.domain.entity;

/**
 * @author Anatoly A. Ivanov
 * 04.06.2018 13:44
 */
public abstract class DomainNode extends Domain{

    public DomainNode(String entityName) {
        super(entityName);
    }

    public DomainNode(Domain domain, String entityName) {
        super(domain, entityName);
    }

    public abstract Long getLeftEntityAttributeId();
    public abstract Long getRightEntityAttributeId();
    public abstract Long getLevelEntityAttributeId();
    public abstract Long getNodeParentEntityAttributeId();

    public Long getLeft() {
        return getNumber(getLeftEntityAttributeId());
    }

    public void setLeft(Long left) {
        setNumber(getLeftEntityAttributeId(), left);
    }

    public Long getRight() {
        return getNumber(getRightEntityAttributeId());
    }

    public void setRight(Long right) {
        setNumber(getRightEntityAttributeId(), right);
    }

    public Long getLevel() {
        return getNumber(getLevelEntityAttributeId());
    }

    public void setLevel(Long level) {
        setNumber(getLevelEntityAttributeId(), level);
    }

    public Long getNodeParentId(){
        return getNumber(getNodeParentEntityAttributeId());
    }
}
