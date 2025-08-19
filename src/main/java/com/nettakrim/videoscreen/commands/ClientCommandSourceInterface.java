package com.nettakrim.videoscreen.commands;

import com.nettakrim.videoscreen.Parameters;

public interface ClientCommandSourceInterface {
    Parameters.Builder videoscreen$getEditingParameters();

    Parameters.Builder videoscreen$getFinalParameters();
}
