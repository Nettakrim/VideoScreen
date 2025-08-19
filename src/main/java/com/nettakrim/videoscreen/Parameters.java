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
        private boolean hasSource;
        private @Nullable String fileSource;
        private @Nullable String urlSource;
        private @Nullable Integer volume;
        private @Nullable Boolean stopInput;
        private @Nullable Float opacity;

        public Builder() {
            hasSource = false;
            fileSource = null;
            urlSource = null;
            volume = null;
            stopInput = null;
            opacity = null;
        }

        public Parameters build() {
            return new Parameters(volume, stopInput, opacity);
        }

        public boolean hasSource() {
            return hasSource;
        }

        public @Nullable String getSource() {
            if (fileSource != null) {
                return fileSource;
            }
            return urlSource;
        }

        public void addFileSource(@Nullable String value) {
            hasSource = true;
            VideoScreenClient.LOGGER.info(value);
            if (value == null || fileSource != null || !new File(value).exists()) {
                VideoScreenClient.LOGGER.info("invalid");
                return;
            }
            fileSource = value;
        }

        public void addUrlSource(@Nullable String value) {
            hasSource = true;
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
