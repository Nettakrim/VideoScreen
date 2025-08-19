package com.nettakrim.videoscreen;

import org.jetbrains.annotations.Nullable;

public class VideoParameters {
    public @Nullable String source;
    public int volume;
    public boolean stopInput;
    public float transparency;

    public VideoParameters() {
        source = null;
        volume = 100;
        stopInput = true;
        transparency = 1f;
    }

    public void setSource(String value) {
        if (source == null) {
            source = value;
        }
    }

    public void setVolume(int value) {
        volume = value;
    }

    public void setStopInput(boolean value) {
        stopInput = value;
    }

    public void setTransparency(float value) {
        transparency = value;
    }
}
