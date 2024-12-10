package com.example.myapplication.model;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Font implements Serializable {
    private String name;
    private int size;
    private float lineSpace;
    private int wordSpace;
    private int letterSpace;

    public Font() {}

    public Font(String name, int size, float lineSpace, int wordSpace, int letterSpace) {
        this.name = name;
        this.size = size;
        this.lineSpace = lineSpace;
        this.wordSpace = wordSpace;
        this.letterSpace = letterSpace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public float getLineSpace() {
        return lineSpace;
    }

    public void setLineSpace(float lineSpace) {
        this.lineSpace = lineSpace;
    }

    public int getWordSpace() {
        return wordSpace;
    }

    public void setWordSpace(int wordSpace) {
        this.wordSpace = wordSpace;
    }

    public int getLetterSpace() {
        return letterSpace;
    }

    public void setLetterSpace(int letterSpace) {
        this.letterSpace = letterSpace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Font font = (Font) o;
        return size == font.size && Float.compare(lineSpace, font.lineSpace) == 0 && wordSpace == font.wordSpace && letterSpace == font.letterSpace && Objects.equals(name, font.name);
    }
}
