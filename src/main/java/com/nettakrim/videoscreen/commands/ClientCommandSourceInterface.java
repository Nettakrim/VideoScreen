package com.nettakrim.videoscreen.commands;

import com.nettakrim.videoscreen.VideoParameters;

public interface ClientCommandSourceInterface {
    VideoParameters videoscreen$getEditingParameters();

    VideoParameters videoscreen$getFinalParameters();
}
