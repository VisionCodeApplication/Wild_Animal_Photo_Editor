package com.wildanimalphoto.photoeditor.models;

public class Animals {
    int animal, thanimal;
    Boolean locked;

    public Animals(int animal, int thanimal, Boolean locked) {
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
