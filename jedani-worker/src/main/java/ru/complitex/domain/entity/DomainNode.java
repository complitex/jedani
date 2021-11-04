package ru.complitex.domain.entity;

/**
 * @author Anatoly A. Ivanov
 * 04.06.2018 13:44
 */
public class DomainNode extends Domain{
    private Long left;
    private Long right;
    private Long level;

    private Long updateLeft;
    private Long updateRight;
    private Long updateLevel;


    public DomainNode() {
    }

    public DomainNode(String entityName) {
        super(entityName);
    }

    public DomainNode(String entityName, Long objectId) {
        super(entityName);

        setObjectId(objectId);
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

    public Long getUpdateLeft() {
        return updateLeft;
    }

    public void setUpdateLeft(Long updateLeft) {
        this.updateLeft = updateLeft;
    }

    public Long getUpdateRight() {
        return updateRight;
    }

    public void setUpdateRight(Long updateRight) {
        this.updateRight = updateRight;
    }

    public Long getUpdateLevel() {
        return updateLevel;
    }

    public void setUpdateLevel(Long updateLevel) {
        this.updateLevel = updateLevel;
    }
}
