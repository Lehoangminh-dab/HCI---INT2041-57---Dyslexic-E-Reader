package com.example.myapplication.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class User implements Serializable {
    private String id;
    private String email;
    private String password;
    private String name;
    private List<ColorRule> ruleList;
    private Font font;
    private String highLight;
    private List<Book> bookList;

    public User() {
    }

    public User(String id, String email, String password, String name, List<ColorRule> ruleList, Font font, String highLight, List<Book> bookList) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.ruleList = ruleList;
        this.font = font;
        this.highLight = highLight;
        this.bookList = bookList;
    }

    public User(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.name = user.getName();
        this.ruleList = user.getRuleList();
        this.font = user.getFont();
        this.highLight = user.getHighLight();
        this.bookList = user.getBookList();
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

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(name, user.name) && Objects.equals(ruleList, user.ruleList) && Objects.equals(font, user.font) && Objects.equals(highLight, user.highLight) && Objects.equals(bookList, user.bookList);
    }
}
