package com.nettakrim.videoscreen;

import org.jetbrains.annotations.Nullable;

public class VideoParameters {
    public @Nullable String source;
    public int volume;

    public VideoParameters() {
        source = null;
        volume = 100;
    }

    public void setSource(String value) {
        if (source == null) {
            source = value;
        }
    }

    public void setVolume(int value) {
        volume = value;
    }
}
