package com.wildanimalphoto.photoeditor.models;

public class Frames {
    int frame, thframe;
    Boolean locked;

    public Frames(int frame, int thframe, Boolean locked) {
        this.frame = frame;
        this.thframe = thframe;
        this.locked = locked;
    }

    public int getFrame() {
        return frame;
    }

    public int getthframe() {
        return thframe;
    }

    public void setFrame(int suit) {
        this.frame = suit;
    }

    public void setthframe(int thframe) {
        this.thframe = thframe;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
}
