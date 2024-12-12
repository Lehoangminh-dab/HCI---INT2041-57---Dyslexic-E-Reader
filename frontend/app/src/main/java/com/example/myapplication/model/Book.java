package com.example.myapplication.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringTokenizer;

public class Book implements Serializable {
    private int id;
    private String title, author, sum, content;
    private int totalWord;
    private String isFavourite;
    private String isComplete;
    private String isOurBook;
    private String folder;

    public Book() {
    }

    public Book(String content) {
        id = hashCode();
        content.trim();
        if (content.isEmpty()) {
            return;
        }
        this.content = content;
        author = "you";
        title = "Book " + id;
        StringTokenizer tokenizer = new StringTokenizer(content);
        totalWord = tokenizer.countTokens();
        sum = content.substring(0, 60).trim() + "...";
        isFavourite = "false";
        isComplete = "false";
        isOurBook = "false";
    }

    public Book(Book book) {
        this.folder = book.folder;
        this.isOurBook = book.isOurBook;
        this.isComplete = book.isComplete;
        this.isFavourite = book.isFavourite;
        this.totalWord = book.totalWord;
        this.content = book.content;
        this.sum = book.sum;
        this.author = book.author;
        this.title = book.title;
        id = hashCode();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(String isFavourite) {
        this.isFavourite = isFavourite;
    }

    public String getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(String isComplete) {
        this.isComplete = isComplete;
    }

    public String getIsOurBook() {
        return isOurBook;
    }

    public void setIsOurBook(String isOurBook) {
        this.isOurBook = isOurBook;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return totalWord == book.totalWord && isFavourite == book.isFavourite && isComplete == book.isComplete && isOurBook == book.isOurBook && Objects.equals(title, book.title) && Objects.equals(author, book.author) && Objects.equals(sum, book.sum) && Objects.equals(content, book.content) && Objects.equals(folder, book.folder);
    }
}

