package com.nettakrim.videoscreen;

import org.jetbrains.annotations.Nullable;

public class Parameters {
    public int volume;
    public boolean stopInput;
    public float opacity;

    public Parameters(@Nullable Integer volume, @Nullable Boolean stopInput, @Nullable Float opacity) {
        this.volume = volume == null ? 100 : volume;
        this.stopInput = stopInput == null || stopInput;
        this.opacity = opacity == null ? 1f : opacity;
    }

    public static class Builder {
        public @Nullable String source;
        public @Nullable Integer volume;
        public @Nullable Boolean stopInput;
        public @Nullable Float opacity;

        public Builder() {
            source = null;
            volume = null;
            stopInput = null;
            opacity = null;
        }

        public Parameters build() {
            return new Parameters(volume, stopInput, opacity);
        }

        public void setSource(@Nullable String value) {
            source = value == null ? "" : value;
        }

        public void setVolume(int value) {
            volume = value;
        }

        public void setStopInput(boolean value) {
            stopInput = value;
        }

        public void setOpacity(float value) {
            opacity = value;
        }
    }
}
