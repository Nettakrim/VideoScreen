package com.nettakrim.videoscreen;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VideoParameters {
    public List<String> sourcePriority;
    public float volume;

    public VideoParameters() {
        sourcePriority = new ArrayList<>();
        volume = 0.5f;
    }

    public @Nullable String getSource() {
        if (sourcePriority.isEmpty()) {
            return null;
        }

        // TODO: fallbacks
        return sourcePriority.getFirst();
    }

    public void addSource(String value) {
        VideoScreenClient.LOGGER.info("adding source "+value);
        if (value != null) {
            sourcePriority.add(value);
        }
    }

    public void setVolume(float value) {
        volume = value;
    }
}
