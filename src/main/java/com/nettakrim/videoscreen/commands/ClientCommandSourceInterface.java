package com.nettakrim.videoscreen.commands;

import com.nettakrim.videoscreen.VideoParameters;
import org.jetbrains.annotations.NotNull;

public interface ClientCommandSourceInterface {
    @NotNull
    VideoParameters.Builder videoscreen$getParameters();

    void videoscreen$clearParameters();
}
