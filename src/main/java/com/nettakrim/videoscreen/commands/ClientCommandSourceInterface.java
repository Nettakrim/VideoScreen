package com.nettakrim.videoscreen.commands;

import com.nettakrim.videoscreen.Parameters;
import org.jetbrains.annotations.NotNull;

public interface ClientCommandSourceInterface {
    @NotNull
    Parameters.Builder videoscreen$getEditingParameters();

    @NotNull
    Parameters.Builder videoscreen$getFinalParameters();
}
