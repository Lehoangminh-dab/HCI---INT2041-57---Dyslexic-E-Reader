package com.example.myapplication.model;

import java.io.Serializable;
import java.util.Objects;

public class ColorRule implements Serializable {
    private int id;
    private String name;
    private String describe;
    private int color;
    private boolean isDefault;

    public ColorRule() {
    }

    public ColorRule(String name, String describe, int color) {
        id = hashCode();
        this.name = name;
        this.describe = describe;
        this.color = color;
        isDefault = false;
    }

    public void setValues(String name, String describe, int color) {
        setName(name);
        setDescribe(describe);
        setColor(color);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColorRule colorRule = (ColorRule) o;
        return id == colorRule.id && color == colorRule.color && isDefault == colorRule.isDefault && Objects.equals(name, colorRule.name) && Objects.equals(describe, colorRule.describe);
    }
}
