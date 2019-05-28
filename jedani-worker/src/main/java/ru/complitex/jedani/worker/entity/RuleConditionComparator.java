package ru.complitex.jedani.worker.entity;

import ru.complitex.common.entity.IdEnum;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public enum RuleConditionComparator implements IdEnum {
    EQUAL(1L, "=="),
    NOT_EQUAL(2L, "!="),
    GREATER(3L, ">"),
    LOWER(4L, "<"),
    GREATER_OR_EQUAL(5L, ">="),
    LOWER_OR_EQUAL(6L, "<=");

    private Long id;
    private String text;

    RuleConditionComparator(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public static RuleConditionComparator getValue(Long id){
        if (id == null){
            return null;
        }

        //noinspection OptionalGetWithoutIsPresent
        return Arrays.stream(values()).filter(t -> Objects.equals(id, t.getId())).findAny().get();
    }

    public static List<Long> getIds(){
        return Arrays.stream(values()).map(RuleConditionComparator::getId).collect(Collectors.toList());
    }
}
