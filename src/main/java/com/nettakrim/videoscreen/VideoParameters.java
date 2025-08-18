package com.nettakrim.videoscreen;

import org.jetbrains.annotations.Nullable;

public class VideoParameters {
    public @Nullable String source;
    public float volume;

    public VideoParameters() {
        source = null;
        volume = 0.5f;
    }

    public void setSource(String value) {
        if (source == null) {
            source = value;
        }
    }

    public void setVolume(float value) {
        volume = value;
    }
}
