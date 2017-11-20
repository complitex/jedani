package ru.complitex.jedani.entity;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 20.11.2017 16:41
 */
public class City implements Serializable{
    private Integer id;
    private String name;
    private Integer regionId;
    private Integer managerId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }
}
