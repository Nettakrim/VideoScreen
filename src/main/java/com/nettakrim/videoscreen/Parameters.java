package com.nettakrim.videoscreen;

import org.jetbrains.annotations.Nullable;

import java.io.File;

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
        private @Nullable String fileSource;
        private @Nullable String urlSource;
        private @Nullable Integer volume;
        private @Nullable Boolean stopInput;
        private @Nullable Float opacity;

        private final boolean isSettings;

        public Builder(boolean isSettings) {
            fileSource = null;
            urlSource = null;
            volume = null;
            stopInput = null;
            opacity = null;

            this.isSettings = isSettings;
        }

        public Parameters build() {
            return new Parameters(volume, stopInput, opacity);
        }

        public boolean isSettings() {
            return isSettings;
        }

        public @Nullable String getSource() {
            if (fileSource != null) {
                return fileSource;
            }
            return urlSource;
        }

        public void addFileSource(@Nullable String value) {
            if (value == null || fileSource != null || !new File(value).exists()) {
                return;
            }
            fileSource = value;
        }

        public void addUrlSource(@Nullable String value) {
            if (value == null || urlSource != null) {
                return;
            }
            urlSource = value;
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

        public void updateParameters(Parameters parameters) {
            if (volume != null) {
                parameters.volume = volume;
            }
            if (stopInput != null) {
                parameters.stopInput = stopInput;
            }
            if (opacity != null) {
                parameters.opacity = opacity;
            }
        }
    }
}
