package com.wildanimalphoto.photoeditor.models;

public class FramesModel {
    int frameId;
    int thumbId;

    private int effect_thumb;

    public FramesModel(int frameId, int thumbId) {
        this.frameId = frameId;
        this.thumbId = thumbId;
    }

    public int getFrameId() {
        return frameId;
    }

    public void setFrameId(int frameId) {
        this.frameId = frameId;
    }

    public int getThumbId() {
        return thumbId;
    }

    public void setThumbId(int thumbId) {
        this.thumbId = thumbId;
    }

    public int getEffect_thumb() {
        return effect_thumb;
    }

    public void setEffect_thumb(int effect_thumb) {
        this.effect_thumb = effect_thumb;
    }

    public FramesModel(int theme_thumb) {
        this.effect_thumb=theme_thumb;
    }
}
