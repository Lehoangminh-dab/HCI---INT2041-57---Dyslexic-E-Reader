package com.example.myapplication.model;

import java.io.Serializable;
import java.util.Objects;

public class Book implements Serializable {
    private int id;
    private String title, author, sum, content;
    private int totalWord;


    public Book() {
    }

    public Book(String title, int totalWord) {
        id = hashCode();
        this.title = title;
        this.totalWord = totalWord;

    }
    public Book(String title, int totalWord, String author, String sum, String content) {
        id = hashCode();
        this.title = title;
        this.totalWord = totalWord;
        this.author = author;
        this.sum = sum;
        this.content = content;
    }

    public void setValues(String title, int totalWord) {
        setTitle(title);
        setTotalWord(totalWord);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotalWord() {return totalWord;}

    public void setTotalWord(int totalWord) {this.totalWord = totalWord;}

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id && totalWord == book.totalWord;
    }
}

