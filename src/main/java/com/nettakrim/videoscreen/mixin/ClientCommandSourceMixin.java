package com.nettakrim.videoscreen.mixin;

import com.nettakrim.videoscreen.VideoParameters;
import com.nettakrim.videoscreen.commands.ClientCommandSourceInterface;
import net.minecraft.client.network.ClientCommandSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientCommandSource.class)
public class ClientCommandSourceMixin implements ClientCommandSourceInterface {
    @Unique @Nullable
    VideoParameters.Builder parameters;

    @Override
    public VideoParameters.@NotNull Builder videoscreen$getParameters() {
        if (parameters == null) {
            parameters = new VideoParameters.Builder();
        }
        return parameters;
    }

    @Override
    public void videoscreen$clearParameters() {
        parameters = null;
    }
}
