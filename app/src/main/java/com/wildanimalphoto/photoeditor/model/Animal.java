package com.wildanimalphoto.photoeditor.model;

public class Animal {
    int animal, thanimal;
    Boolean locked;

    public Animal(int animal, int thanimal, Boolean locked) {
        this.animal = animal;
        this.thanimal = thanimal;
        this.locked = locked;
    }

    public int getanimal() {
        return animal;
    }

    public int getthanimal() {
        return thanimal;
    }

    public void setanimal(int suit) {
        this.animal = suit;
    }

    public void setthanimal(int thanimal) {
        this.thanimal = thanimal;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
}
