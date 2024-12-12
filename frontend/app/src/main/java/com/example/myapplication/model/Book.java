package com.example.myapplication.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringTokenizer;

public class Book implements Serializable {
    private int id;
    private String title, author, sum, content;
    private int totalWord;
    private boolean isFavourite;
    private boolean isComplete;
    private boolean isOurBook;
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
        StringTokenizer tokenizer = new StringTokenizer(content);
        this.totalWord = tokenizer.countTokens();
        this.sum = content.substring(0, 60).trim() + "...";
        isFavourite = false;
        isComplete = false;
        isOurBook = false;
    }

    public Book(String title, String author, String sum, String content) {
        id = hashCode();
        content.trim();
        if (content.isEmpty()) {
            return;
        }
        this.content = content;
        StringTokenizer tokenizer = new StringTokenizer(content);
        this.totalWord = tokenizer.countTokens();
        this.sum = sum;
        this.title = title;
        this.author = author;
        isFavourite = false;
        isComplete = false;
        isOurBook = false;
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

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public boolean isOurBook() {
        return isOurBook;
    }

    public void setOurBook(boolean ourBook) {
        isOurBook = ourBook;
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

