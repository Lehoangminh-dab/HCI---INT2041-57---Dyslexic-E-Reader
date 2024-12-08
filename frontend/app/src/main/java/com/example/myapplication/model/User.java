package com.example.myapplication.model;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String id;
    private String email;
    private String password;
    private String name;
    private List<ColorRule> ruleList;
    private Font font;
    private String highLight;
    private List<Book> books;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHighLight() {
        return highLight;
    }

    public void setHighLight(String highLight) {
        this.highLight = highLight;
    }

    public User(String id, String email, String password, String name, List<ColorRule> ruleList) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.ruleList = ruleList;
        font = null;
        highLight = null;
        books = null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ColorRule> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<ColorRule> ruleList) {
        this.ruleList = ruleList;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
